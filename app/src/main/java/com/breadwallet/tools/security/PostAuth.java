package com.breadwallet.tools.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;

import com.breadwallet.R;
import com.breadwallet.core.BRCoreChainParams;
import com.breadwallet.core.BRCoreKey;
import com.breadwallet.core.BRCoreMasterPubKey;
import com.breadwallet.core.BRCoreTransaction;
import com.breadwallet.presenter.activities.SetPinActivity;
import com.breadwallet.presenter.activities.PaperKeyActivity;
import com.breadwallet.presenter.activities.PaperKeyProveActivity;
import com.breadwallet.presenter.activities.intro.WriteDownActivity;
import com.breadwallet.presenter.activities.util.ActivityUTILS;
import com.breadwallet.presenter.entities.CryptoRequest;
import com.breadwallet.tools.animation.BRDialog;
import com.breadwallet.tools.manager.BRReportsManager;
import com.breadwallet.tools.manager.BRSharedPrefs;
import com.breadwallet.tools.sqlite.CurrencyDataSource;
import com.breadwallet.tools.threads.executor.BRExecutor;
import com.breadwallet.tools.util.BRConstants;
import com.breadwallet.tools.util.CurrencyUtils;
import com.breadwallet.tools.util.Utils;
import com.breadwallet.wallet.WalletsMaster;
import com.breadwallet.wallet.abstracts.BaseWalletManager;
import com.platform.entities.TxMetaData;
import com.platform.tools.BRBitId;
import com.platform.tools.KVStoreManager;

import java.util.Arrays;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 4/14/16.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class PostAuth {
    public static final String TAG = PostAuth.class.getName();

    private String phraseForKeyStore;
    public CryptoRequest mCryptoRequest;
    public static boolean isStuckWithAuthLoop;

    private BRCoreTransaction mPaymentProtocolTx;
    private static PostAuth instance;

    private PostAuth() {
    }

    public static PostAuth getInstance() {
        if (instance == null) {
            instance = new PostAuth();
        }
        return instance;
    }

    public void onCreateWalletAuth(Activity app, boolean authAsked) {
        Log.e(TAG, "onCreateWalletAuth: " + authAsked);
        long start = System.currentTimeMillis();
        boolean success = WalletsMaster.getInstance(app).generateRandomSeed(app);
        if (success) {
            WalletsMaster.getInstance(app).initWallets(app);
            Intent intent = new Intent(app, WriteDownActivity.class);
            app.startActivity(intent);
            app.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            if (authAsked) {
                Log.e(TAG, "onCreateWalletAuth: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
    }

    public void onPhraseCheckAuth(Activity app, boolean authAsked) {
        String cleanPhrase;
        try {
            byte[] raw = BRKeyStore.getPhrase(app, BRConstants.SHOW_PHRASE_REQUEST_CODE);
            if (raw == null) {
                BRReportsManager.reportBug(new NullPointerException("onPhraseCheckAuth: getPhrase = null"), true);
                return;
            }
            cleanPhrase = new String(raw);
        } catch (UserNotAuthenticatedException e) {
            if (authAsked) {
                Log.e(TAG, "onPhraseCheckAuth: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
        Intent intent = new Intent(app, PaperKeyActivity.class);
        intent.putExtra("phrase", cleanPhrase);
        app.startActivity(intent);
        app.overridePendingTransition(R.anim.enter_from_bottom, R.anim.empty_300);
    }

    public void onPhraseProveAuth(Activity app, boolean authAsked) {
        String cleanPhrase;
        try {
            cleanPhrase = new String(BRKeyStore.getPhrase(app, BRConstants.PROVE_PHRASE_REQUEST));
        } catch (UserNotAuthenticatedException e) {
            if (authAsked) {
                Log.e(TAG, "onPhraseProveAuth: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
        Intent intent = new Intent(app, PaperKeyProveActivity.class);
        intent.putExtra("phrase", cleanPhrase);
        app.startActivity(intent);
        app.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void onBitIDAuth(Activity app, boolean authenticated) {
        BRBitId.completeBitID(app, authenticated);
    }

    public void onRecoverWalletAuth(Activity app, boolean authAsked) {
        if (Utils.isNullOrEmpty(phraseForKeyStore)) {
            Log.e(TAG, "onRecoverWalletAuth: phraseForKeyStore is null or empty");
            BRReportsManager.reportBug(new NullPointerException("onRecoverWalletAuth: phraseForKeyStore is or empty"));
            return;
        }
        byte[] bytePhrase = new byte[0];

        try {
            boolean success = false;
            try {
                success = BRKeyStore.putPhrase(phraseForKeyStore.getBytes(),
                        app, BRConstants.PUT_PHRASE_RECOVERY_WALLET_REQUEST_CODE);
            } catch (UserNotAuthenticatedException e) {
                if (authAsked) {
                    Log.e(TAG, "onRecoverWalletAuth: WARNING!!!! LOOP");
                    isStuckWithAuthLoop = true;

                }
                return;
            }

            if (!success) {
                if (authAsked) {
                    Log.e(TAG, "onRecoverWalletAuth, !success && authAsked");
                }
            } else {
                if (phraseForKeyStore.length() != 0) {
                    BRSharedPrefs.putPhraseWroteDown(app, true);
                    byte[] seed = BRCoreKey.getSeedFromPhrase(phraseForKeyStore.getBytes());
                    byte[] authKey = BRCoreKey.getAuthPrivKeyForAPI(seed);
                    BRKeyStore.putAuthKey(authKey, app);
                    BRCoreMasterPubKey mpk = new BRCoreMasterPubKey(phraseForKeyStore.getBytes(), true);
                    BRKeyStore.putMasterPublicKey(mpk.serialize(), app);
                    app.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    Intent intent = new Intent(app, SetPinActivity.class);
                    intent.putExtra("noPin", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    app.startActivity(intent);
                    if (!app.isDestroyed()) app.finish();
                    phraseForKeyStore = null;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            BRReportsManager.reportBug(e);
        } finally {
            Arrays.fill(bytePhrase, (byte) 0);
        }

    }

    public void onPublishTxAuth(final Context app, boolean authAsked) {
        if (ActivityUTILS.isMainThread()) throw new NetworkOnMainThreadException();

        final BaseWalletManager walletManager = WalletsMaster.getInstance(app).getCurrentWallet(app);
        byte[] rawPhrase;
        try {
            rawPhrase = BRKeyStore.getPhrase(app, BRConstants.PAY_REQUEST_CODE);
        } catch (UserNotAuthenticatedException e) {
            if (authAsked) {
                Log.e(TAG, "onPublishTxAuth: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
        if (rawPhrase.length < 10) return;
        try {
            if (rawPhrase.length != 0) {
                if (mCryptoRequest != null && mCryptoRequest.tx != null) {

                    byte[] txHash = walletManager.signAndPublishTransaction(mCryptoRequest.tx, rawPhrase);
                    if (Utils.isNullOrEmpty(txHash)) {
                        Log.e(TAG, "onPublishTxAuth: signAndPublishTransaction returned an empty txHash");
                        BRDialog.showSimpleDialog(app, "Send failed", "signAndPublishTransaction failed");
                        //todo fix this
//                        WalletsMaster.getInstance(app).offerToChangeTheAmount(app, new PaymentItem(paymentRequest.addresses, paymentItem.serializedTx, paymentRequest.amount, null, paymentRequest.isPaymentRequest));
                    } else {
                        TxMetaData txMetaData = new TxMetaData();
                        txMetaData.comment = mCryptoRequest.message;
                        txMetaData.exchangeCurrency = BRSharedPrefs.getPreferredFiatIso(app);
                        txMetaData.exchangeRate = CurrencyDataSource.getInstance(app).getCurrencyByCode(app, walletManager.getIso(app), txMetaData.exchangeCurrency).rate;
                        txMetaData.fee = walletManager.getWallet().getTransactionFee(mCryptoRequest.tx);
                        txMetaData.txSize = (int) mCryptoRequest.tx.getSize();
                        txMetaData.blockHeight = BRSharedPrefs.getLastBlockHeight(app, walletManager.getIso(app));
                        txMetaData.creationTime = (int) (System.currentTimeMillis() / 1000);//seconds
                        txMetaData.deviceId = BRSharedPrefs.getDeviceId(app);
                        txMetaData.classVersion = 1;
                        KVStoreManager.getInstance().putTxMetaData(app, txMetaData, txHash);
                    }
                    mCryptoRequest = null;
                } else {
                    throw new NullPointerException("payment item is null");
                }
            } else {
                Log.e(TAG, "onPublishTxAuth: seed length is 0!");
                return;
            }
        } finally {
            Arrays.fill(rawPhrase, (byte) 0);
        }

    }


    public void onPaymentProtocolRequest(final Activity app, boolean authAsked) {
        final byte[] rawSeed;
        try {
            rawSeed = BRKeyStore.getPhrase(app, BRConstants.PAYMENT_PROTOCOL_REQUEST_CODE);
        } catch (UserNotAuthenticatedException e) {
            if (authAsked) {
                Log.e(TAG, "onPaymentProtocolRequest: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
        if (rawSeed == null || rawSeed.length < 10 || mPaymentProtocolTx == null) {
            Log.d(TAG, "onPaymentProtocolRequest() returned: rawSeed is malformed: " + (rawSeed == null ? "" : rawSeed.length));
            return;
        }
        if (rawSeed.length < 10) return;


        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                byte[] txHash = WalletsMaster.getInstance(app).getCurrentWallet(app).signAndPublishTransaction(mPaymentProtocolTx, rawSeed);
                if (Utils.isNullOrEmpty(txHash)) throw new NullPointerException("txHash is null!");
//                PaymentProtocolPostPaymentTask.sent = true;
                Arrays.fill(rawSeed, (byte) 0);
                mPaymentProtocolTx = null;
            }
        });

    }

    public void setPhraseForKeyStore(String phraseForKeyStore) {
        this.phraseForKeyStore = phraseForKeyStore;
    }


    public void setPaymentItem(CryptoRequest cryptoRequest) {
        this.mCryptoRequest = cryptoRequest;
    }

    public void setTmpPaymentRequestTx(BRCoreTransaction tx) {
        this.mPaymentProtocolTx = tx;
    }

    public void onCanaryCheck(final Activity app, boolean authAsked) {
        String canary = null;
        try {
            canary = BRKeyStore.getCanary(app, BRConstants.CANARY_REQUEST_CODE);
        } catch (UserNotAuthenticatedException e) {
            if (authAsked) {
                Log.e(TAG, "onCanaryCheck: WARNING!!!! LOOP");
                isStuckWithAuthLoop = true;
            }
            return;
        }
        if (canary == null || !canary.equalsIgnoreCase(BRConstants.CANARY_STRING)) {
            byte[] phrase;
            try {
                phrase = BRKeyStore.getPhrase(app, BRConstants.CANARY_REQUEST_CODE);
            } catch (UserNotAuthenticatedException e) {
                if (authAsked) {
                    Log.e(TAG, "onCanaryCheck: WARNING!!!! LOOP");
                    isStuckWithAuthLoop = true;
                }
                return;
            }

            String strPhrase = new String((phrase == null) ? new byte[0] : phrase);
            if (strPhrase.isEmpty()) {
                WalletsMaster m = WalletsMaster.getInstance(app);
                m.wipeKeyStore(app);
                m.wipeWalletButKeystore(app);
            } else {
                Log.e(TAG, "onCanaryCheck: Canary wasn't there, but the phrase persists, adding canary to keystore.");
                try {
                    BRKeyStore.putCanary(BRConstants.CANARY_STRING, app, 0);
                } catch (UserNotAuthenticatedException e) {
                    if (authAsked) {
                        Log.e(TAG, "onCanaryCheck: WARNING!!!! LOOP");
                        isStuckWithAuthLoop = true;
                    }
                    return;
                }
            }
        }
        WalletsMaster.getInstance(app).startTheWalletIfExists(app);
    }


}
