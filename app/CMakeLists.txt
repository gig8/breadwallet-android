# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

# now build app's shared lib
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -DANDROID_STL=gnustl_static -DANDROID_TOOLCHAIN=clang")

# -Wimplicit-function-declaration
# -Wno-missing-prototypes -Werror=return-type -Wdocumentation -Wunreachable-code-aggressive -Wno-missing-braces
# -Wparentheses -Wswitch -Wno-unused-function -Wunused-label -Wno-unused-parameter -Wunused-variable -Wunused-value -Wempty-body
# -Wconditional-uninitialized -Wno-unknown-pragmas -pedantic -Wshadow -Wfour-char-constants -Wno-conversion -Wconstant-conversion
# -Wint-conversion -Wbool-conversion -Wenum-conversion -Wassign-enum -Wno-shorten-64-to-32 -Wpointer-sign -Wnewline-eof
# -Wdeprecated-declarations -Wno-sign-conversion

add_library( # The name of the library.
             core

             # Shared library.
             SHARED

             # Core files
             src/main/jni/breadwallet-core/BRAddress.c
             src/main/jni/breadwallet-core/BRAddress.h
             src/main/jni/breadwallet-core/BRArray.h
             src/main/jni/breadwallet-core/BRBase58.c
             src/main/jni/breadwallet-core/BRBase58.h
             src/main/jni/breadwallet-core/BRBIP32Sequence.c
             src/main/jni/breadwallet-core/BRBIP32Sequence.h
             src/main/jni/breadwallet-core/BRBIP38Key.c
             src/main/jni/breadwallet-core/BRBIP38Key.h
             src/main/jni/breadwallet-core/BRBIP39Mnemonic.c
             src/main/jni/breadwallet-core/BRBIP39Mnemonic.h
             src/main/jni/breadwallet-core/BRBIP39WordsEn.h
             src/main/jni/breadwallet-core/BRBloomFilter.c
             src/main/jni/breadwallet-core/BRBloomFilter.h
             src/main/jni/breadwallet-core/BRCrypto.c
             src/main/jni/breadwallet-core/BRCrypto.h
             src/main/jni/breadwallet-core/BRInt.h
             src/main/jni/breadwallet-core/BRKey.c
             src/main/jni/breadwallet-core/BRKey.h
             src/main/jni/breadwallet-core/BRMerkleBlock.c
             src/main/jni/breadwallet-core/BRMerkleBlock.h
             src/main/jni/breadwallet-core/BRPaymentProtocol.c
             src/main/jni/breadwallet-core/BRPaymentProtocol.h
             src/main/jni/breadwallet-core/BRPeer.c
             src/main/jni/breadwallet-core/BRPeer.h
             src/main/jni/breadwallet-core/BRPeerManager.c
             src/main/jni/breadwallet-core/BRPeerManager.h
             src/main/jni/breadwallet-core/BRSet.c
             src/main/jni/breadwallet-core/BRSet.h
             src/main/jni/breadwallet-core/BRTransaction.c
             src/main/jni/breadwallet-core/BRTransaction.h
             src/main/jni/breadwallet-core/BRWallet.c
             src/main/jni/breadwallet-core/BRWallet.h

             # BCash files
             src/main/jni/breadwallet-core/bcash/BRBCashAddr.c
             src/main/jni/breadwallet-core/bcash/BRBCashAddr.h
             src/main/jni/breadwallet-core/bcash/BRBCashParams.h

             # Mota files
             src/main/jni/breadwallet-core/mota/BRMotaParams.h
             src/main/jni/breadwallet-core/mota/BRX13Crypto.c
             src/main/jni/breadwallet-core/mota/BRX13Crypto.h

             # X11 files
             src/main/jni/breadwallet-core/crypto/aes_helper.c
             src/main/jni/breadwallet-core/crypto/blake.c
             src/main/jni/breadwallet-core/crypto/bmw.c
             src/main/jni/breadwallet-core/crypto/cubehash.c
             src/main/jni/breadwallet-core/crypto/echo.c
             src/main/jni/breadwallet-core/crypto/fugue.c
             src/main/jni/breadwallet-core/crypto/groestl.c
             src/main/jni/breadwallet-core/crypto/hamsi_helper.c
             src/main/jni/breadwallet-core/crypto/hamsi.c
             src/main/jni/breadwallet-core/crypto/jh.c
             src/main/jni/breadwallet-core/crypto/keccak.c
             src/main/jni/breadwallet-core/crypto/luffa.c
             src/main/jni/breadwallet-core/crypto/shavite.c
             src/main/jni/breadwallet-core/crypto/simd.c
             src/main/jni/breadwallet-core/crypto/skein.c
             src/main/jni/breadwallet-core/crypto/sph_blake.h
             src/main/jni/breadwallet-core/crypto/sph_bmw.h
             src/main/jni/breadwallet-core/crypto/sph_cubehash.h
             src/main/jni/breadwallet-core/crypto/sph_echo.h
             src/main/jni/breadwallet-core/crypto/sph_fugue.h
             src/main/jni/breadwallet-core/crypto/sph_groestl.h
             src/main/jni/breadwallet-core/crypto/sph_hamsi.h
             src/main/jni/breadwallet-core/crypto/sph_jh.h
             src/main/jni/breadwallet-core/crypto/sph_keccak.h
             src/main/jni/breadwallet-core/crypto/sph_luffa.h
             src/main/jni/breadwallet-core/crypto/sph_shavite.h
             src/main/jni/breadwallet-core/crypto/sph_simd.h
             src/main/jni/breadwallet-core/crypto/sph_skein.h
             src/main/jni/breadwallet-core/crypto/sph_types.h

             # Core JNI files
             src/main/jni/breadwallet-core/Java/BRCoreJni.h
             src/main/jni/breadwallet-core/Java/BRCoreJni.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreJniReference.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreJniReference.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreAddress.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreAddress.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreChainParams.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreChainParams.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePeer.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePeer.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreMasterPubKey.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreMasterPubKey.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreKey.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreKey.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreMerkleBlock.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreMerkleBlock.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePeerManager.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePeerManager.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransaction.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransaction.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransactionInput.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransactionInput.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransactionOutput.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreTransactionOutput.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePaymentProtocol.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCorePaymentProtocol.h
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreWallet.c
             src/main/jni/breadwallet-core/Java/com_breadwallet_core_BRCoreWallet.h

             # Bech32 files
             src/main/jni/breadwallet-core/BRBech32.c
             src/main/jni/breadwallet-core/BRBech32.h

             )

include_directories(src/main/jni/breadwallet-core/bcash)
include_directories(src/main/jni/breadwallet-core/mota)
include_directories(src/main/jni/breadwallet-core/Java)
include_directories(src/main/jni/breadwallet-core/)
include_directories(src/main/jni/breadwallet-core/secp256k1/include)
include_directories(src/main/jni/breadwallet-core/secp256k1/src)
include_directories(src/main/jni/breadwallet-core/secp256k1)

include_directories(src/main/secp/secp256k1/include/)
include_directories(src/main/secp/secp256k1/src/)
include_directories(src/main/secp/secp256k1/)
include_directories(src/main/secp/)

# add lib dependencies
target_link_libraries(
    core
    android
    log)
