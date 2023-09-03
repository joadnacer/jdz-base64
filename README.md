This library contains Base64 encoders and decoders implemented using the incubator Vector API, as well as (slower) scalar implementations. This is the fastest Java-written Base64 library, with both the vector-based and scalar methods outperforming any other Java written library. These methods are faster than intrinsified java.util for RFC2045/MIME encoding/decoding, but slower for RFC4648 (see "java.util Intrinsification" and "Benchmark" sections). Requires Java 16 or higher.

The encoding and decoding methods used for the Vector implementation, which can be found in VectorUtils.java, were heavily inspired by Wojciech Mula's articles, available here:

http://0x80.pl/notesen/2016-01-12-sse-base64-encoding.html

http://0x80.pl/notesen/2016-01-17-sse-base64-decoding.html

# Usage
As the Vector API is currently an incubator project, you will need to add the flag `--add-modules jdk.incubator.vector` to your java run command in order to use this library's vector methods.

This library will be hosted on the central Maven repository once the Vector API is officially released. In the meanwhile this may only be used via local installation.

## Decoder/Encoder Instantiation
For convenience, statically instantiated encoders/decoders can be accessed via the Base64Vector and Base64Scalar classes. You may also instantiate the indidual encoders/decoders yourself.

## Fast vs Normal Decode
The fast decode methods assume that input is valid Base64 encoded according to the relevant spec. The normal decode methods do not, and handle invalid input explicitly.

For RFC4648 decoding, the normal decode methods will throw an IllegalArgumentException in the case that an invalid byte is detected, with an exception message that includes the position of the first invalid byte. With RFC2045 decoding, the invalid input bytes are simply ignored (as long as total valid input length is divisible by 4).

Using the fast decoding methods on invalid Base64 input will likely result in either an ArrayOutOfBoundsException due to invalid lookup table indices/invalid encoded input length, or in silently erroneous output.

## Vector Sizing
Java should select the optimal vector size thanks to the use of the Vector API's SPECIES_PREFERRED. However, this may not select the optimal vector size on certain systems.

The automated vector length selection can be overriden with the `jdz.vector.size` system property, which may be set to values 128, 256 or 512, representing the vector bit size. Selecting a value greater than what your CPU supports will make encoding/decoding 100-1000x slower. Experiment with this value if the library appears suspiciously slow.

# java.util Intrinsification

The java.util `encode` and `decode` methods rely on the use of an `encodeBlock` and `decodeBlock` method. These methods are both intrinsinc candidates since Java 11 and 16 respectively, meaning that the JVM can replace them with [an extremely fast SIMD method](https://github.com/openjdk/jdk/blob/a59c9b2ac277d6ff6be1700d91ff389f137e61ca/src/hotspot/cpu/x86/stubGenerator_x86_64.cpp#L6063). From my testing, these methods get intrinsifed after around 5000 calls in a JMH benchmark. So how many bytes need to be decoded in order to hit 5000 calls to these methods?

If you're encoding or decoding according the RFC4648/RFC4648_URL spec, the intrinsic methods get called once for each call to `encode` or `decode`, which obviously allows for the fastest speeds as this minimizes callbacks into Java. Unfortunately, this means that the high level methods must be called ~5000 times before users benefit from intrinsification (*). If you are working with large arrays, this can take a while: 33 seconds of continuous encoding of 10MB arrays were required for the java.util encoding to intrinsify on my M1 mac. Similarly, if your application does not encode/decode 5000+ times in its lifetime, you will never benefit from this. If your application is encoding/decoding base64 sparsely rather than continuously, this might also impede intrinsification.

The java.util RFC2045 methods are more consistent in their intrinsification and unaffected by input size. The encoding method will encode data in 76 byte blocks, before return to Java to write linebreak characters, while the decoding method will return to Java on each invalid base64 character (which includes linebreak characters every 76 bytes). Although this guarantees faster intrinsification due to the IntrinsicCandidate methods being called at least once per 76 bytes of input, this slows down the methods considerably, allowing the jdz vector implementation to be faster.

Intrinsification also does not necessarily occur on every system or JVM. For example, the java.util methods were not intrinsified on an AWS m4.large or on my VMWare Ubuntu VM. As a result, jdz methods strongly outperform java.util on any such system, as java.util will be performing scalar encoding/decoding.

# Benchmarks
Benchmarks were all ran on Java 20 (Temurin), using non byte array allocating methods for all libraries supporting this. These are JMH benchmarks consisting of 3 forks of 5 warmups and 5 iterations. All benchmarks were measured for size of unencoded data, input size for encoding and output size for decoding.

The overall theme in these benchmarks is that it is hard to predict relative performance of jdz vs java.util across different architectures. If your application is dependent on efficient base64 encoding, make sure to test your system yourself to determine what is fastest.

jdz scalar methods are generally the best performing scalar methods for both specs and all architectures - however, these were mostly implemented for reference, and are only worth using over the vector/java.util methods if your cpu does not support 128+ bit vector sizes/does not intrinsify java.util.

As a quick summary, the following table presents the generally fastest library for each spec/method combo. The overall benchmarks assume that java.util is intrinsified - if this is not the case, the jdz vector methods will always be fastest.

| Spec/Method | Overall Encoding     | Overall Fast Decoding | Overall Decoding | Scalar Encoding | Scalar Decoding | Scalar Fast Decoding |
|-------------|----------------------|-----------------------|------------------|-----------------|-----------------|----------------------|
| RFC4648     | java.util            | java.util             | java.util        | jdz scalar      | jdz scalar      | jdz scalar           |
| RFC2045     | jdz vector/java.util | jdz vector            | jdz vector       | jdz scalar      | jdz scalar/java.util | jdz scalar           |

## Throughput
These benchmarks represent MB/s throughput of encoding/decoding methods given 1MB inputs. java.util scalar throughput was meansured by disabling intrinsification.

### RFC4648
#### RFC4648 Encoding
|                                 | java.util scalar | java.util intrinsic | jdz vector | jdz scalar | migBase64 | iharder  | Apache Commons |
|---------------------------------|------------------|---------------------|------------|------------|-----------|----------|----------------|
| Apple M1 (128 bits)             | 1707MB/s         | 16126MB/s           | 6277MB/s   | 2845MB/s   | 1793MB/s  | 1560MB/s | 165MB/s        |
| Vs Java Scalar                  | 1.00x            | 9.45x               | 3.68x      | 1.67x      | 1.05x     | 0.91x    | 0.10x          |
| vs Java Intrinsic               | 0.11x            | 1.00x               | 0.39x      | 0.18x      | 0.11x     | 0.10x    | 0.01x          |
| m4.large (256 bits)             | 886MB/s          | N/A                 | 1515MB/s   | 1171MB/s   | 609MB/s   | 567MB/s  | 133MB/s        |
| vs Java Scalar                  | 1.0x             | N/A                 | 1.70x      | 1.32x      | 0.69x     | 0.64x    | 0.15x          |
| vs Java Intrinsic               | N/A              | N/A                 | N/A        | N/A        | N/A       | N/A      | N/A            |
| m6i.large | 1441MB/s         | 12644MB/s           | 10891MB/s  | 2018MB/s   | 954MB/s   | 984MB/s  | 178MB/s        |
| vs Java Scalar                  | 1.0x             | 8.77x               | 7.56x      | 1.40x      | 0.66x     | 0.68x    | 0.12x          |
| vs Java Intrinsic               | 0.11x            | 1.0x                | 0.86x      | 0.16x      | 0.06x     | 0.08x    | 0.01x          |

### RFC4648 Decoding
|                                 | java.util scalar | java.util intrinsic | jdz vector | jdz vector fast | jdz scalar | jdz scalar fast | migBase64 | migBase64 fast | iharder | Apache Commons |
|---------------------------------|------------------|---------------------|------------|-----------------|------------|-----------------|-----------|----------------|---------|----------------|
| Apple M1 (128 bits)             | 1478MB/s         | 9149MB/s            | 4613MB/s   | 6801MB/s        | 2503MB/s   | 2777MB/s        | 637MB/s   | 1096MB/s       | 323MB/s | 266MB/s        |
| Vs Java Scalar                  | 1.00x            | 6.19x               | 3.12x      | 4.60x           | 1.69x      | 1.88x           | 0.43x     | 0.74x          | 0.22x   | 0.18x          |
| vs Java Intrinsic               | 0.17x            | 1.00x               | 0.50x      | 0.74x           | 0.27x      | 0.30x           | 0.07x     | 0.12x          | 0.04x   | 0.03x          |
| m4.large (256 bits)             | 799MB/s          | N/A                 | 1453MB/s   | 2434MB/s        | 1105MB/s   | 1066MB/s        | 289MB/s   | 479MB/s        | 139MB/s | 120MB/s        |
| vs Java Scalar                  | 1.00x            | N/A                 | 1.82x      | 0.30x           | 1.38x      | 1.33x           | 0.36x     | 0.60x          | 0.17x   | 0.20x          |
| vs Java Intrinsic               | N/A              | N/A                 | N/A        | N/A             | N/A        | N/A             | N/A       | N/A            | N/A     | N/A            |
| m6i.large | 1360MB/s         | 14884MB/s           | 6585MB/s   | 9258MB/s        | 1270MB/s   | 1351MB/s        | 363MB/s   | 791MB/s        | 218MB/s | 194MB/s        |
| vs Java Scalar                  | 1.00x            | 10.20x              | 4.85x      | 6.80x           | 0.93x      | 0.99x           | 0.26x     | 0.58x          | 0.16x   | 0.14x          |
| vs Java Intrinsic               | 0.09x            | 1.00x               | 0.44x      | 0.62x           | 0.09x      | 0.09x           | 0.02x     | 0.05x          | 0.01x   | 0.01x          |

### RFC2045 
#### RFC2045 Encoding
|                                 | java.util scalar | java.util intrinsic | jdz vector | jdz scalar | migBase64 | iharder  | Apache Commons |
|---------------------------------|------------------|---------------------|------------|------------|-----------|----------|----------------|
| Apple M1 (128 bits)             | 1466MB/s         | 5721MB/s            | 5814MB/s   | 2704MB/s   | 1572MB/s  | 1540MB/s | 171MB/s        |
| Vs Java Scalar                  | 1.0x             | 3.90x               | 3.97x      | 1.84x      | 1.07x     | 1.05x    | 0.12x          |
| vs Java Intrinsic               | 0.26x            | 1.0x                | 1.03x      | 0.48x      | 0.28x     | 0.27x    | 0.03x          |
| m4.large (256 bits)             | 686MB/s          | N/A                 | 1192MB/s   | 1087MB/s   | 464MB/s   | 494MB/s  | 125MB/s        |
| vs Java Scalar                  | 1.0x             | N/A                 | 1.74x      | 1.58x      | 0.67x     | 0.72x    | 0.18x          |
| vs Java Intrinsic               | N/A              | N/A                 | N/A        | N/A        | N/A       | N/A      | N/A            |
| m6i.large | 1129MB/s         | 3526MB/s            | 5722MB/s   | 1859MB/s   | 861MB/s   | 872MB/s  | 178MB/s        |
| vs Java Scalar                  | 1.0x             | 3.12x               | 5.07x      | 1.65x      | 0.76x     | 0.77x    | 0.16x          |
| vs Java Intrinsic               | 0.32x            | 1.0x                | 1.622x     | 0.52x      | 0.24x     | 0.25     | 0.05x          |

#### RFC2045 Decoding
|                                 | java.util scalar | java.util intrinsic | jdz vector | jdz vector fast | jdz scalar | jdz scalar fast | migBase64 | migBase64 fast | iharder | Apache Commons |
|---------------------------------|------------------|---------------------|------------|-----------------|------------|-----------------|-----------|----------------|---------|----------------|
| Apple M1 (128 bits)             | 716MB/s          | 919MB/s             | 1805MB/s   | 6685MB/s        | 757MB/s    | 2728MB/s        | 390MB/s   | 928MB/s        | 305MB/s | 255MB/s        |
| Vs Java Scalar                  | 1.00x            | 1.28x               | 2.52x      | 9.33x           | 1.06x      | 3.81x           | 0.54x     | 1.29x          | 0.42x   | 0.35x          |
| vs Java Intrinsic               | 0.78x            | 1.00x               | 1.96x      | 7.27x           | 0.82x      | 2.97x           | 0.42x     | 1.01x          | 0.33x   | 0.28x          |
| m4.large (256 bits)             | 433MB/s          | N/A                 | 731MB/s    | 1809MB/s        | 412MB/s    | 1157MB/s        | 197MB/s   | 479MB/s        | 138MB/s | 120MB/s        |
| vs Java Scalar                  | 1.00x            | N/A                 | 1.67x      | 4.17x           | 0.95x      | 2.67x           | 0.45x     | 1.10x          | 0.32x   | 0.28x          |
| vs Java Intrinsic               | N/A              | N/A                 | N/A        | N/A             | N/A        | N/A             | N/A       | N/A            | N/A     | N/A            |
| m6i.large | 652MB/s          | 761MB/s             | 1729MB/s   | 5077MB/s        | 583MB/s    | 1351/MB/s       | 393MB/s   | 791MB/s        | 218MB/s | 195MB/s        |
| vs Java Scalar                  | 1.00x            | 1.17x               | 2.65x      | 7.79x           | 0.89x      | 2.07x           | 0.60x     | 1.21x          | 0.33x   | 0.30x          |
| vs Java Intrinsic               | 0.86x            | 1.00x               | 2.27x      | 6.77x           | 0.77x      | 1.77x           | 0.52x     | 1.04x          | 0.29x   | 0.26x          |

## Vector Sizing
This benchmark measures different vector sizes on a system supporting up to 512 bits (m6i.large). As expected, the largest supported vector size is fastest, and this is naturally the one automatically selected via use of SPECIES_PREFERRED.

| Method/Vector Size | 128 bits | 256 bits | 512 bits  |
|--------------------|----------|----------|-----------|
| RFC4648 encode     | 4000MB/s | 6733MB/s | 11157MB/s |
| RFC4648 decode     | 2457MB/s | 3945MB/s | 5491MB/s  |
| RFC4648 decodeFast | 3517MB/s | 5646MB/s | 9128MB/s  |
| RFC2045 encode     | 3099MB/s | 4926MB/s | 5724MB/s  |
| RFC2045 decode     | 1150MB/s | 1482MB/s | 1730MB/s  |
| RFC2045 decodeFast | 2965MB/s | 4224MB/s | 6733MB/s  |

## Unencoded Length Benchmarks
The following benchmarks measure the time per operation to encode/decode a certain unencoded length - lower is better. These were all carried out on an M1 mac using 128 bit vectors.

### RFC4648

#### RFC4648 Encoding

| Method/Unencoded Length | 10B  | 100B | 1KB   | 1MB   | 10MB   | 100MB   |
|-------------------|------|------|-------|-------|--------|---------|
| jdz-scalar encode | 11ns | 48ns | 343ns | 346µs | 3486µs | 34.88ms |
| jdz-vector encode | 11ns | 40ns | 195ns | 142µs | 1447µs | 14.31ms |
| java.util encode  | 11ns | 15ns | 73ns  | 60µs  | 5784µs | 57.81ms |
| migBase64 encode  | 22ns | 64ns | 562ns | 545µs | 5498µs | 55.12ms |

#### RFC4648 Decoding
| Method/Unencoded Length    | 10B  | 100B  | 1KB    | 1MB    | 10MB    | 100MB
|-----------------------|------|-------|--------|--------|---------|---------
| jdz-scalar decode     | 11ns | 55ns  | 409ns  | 405µs  | 4079µs  | 40.76ms |
| jdz-scalar decodeFast | 11ns | 52ns  | 371ns  | 364µs  | 3673µs  | 36.79ms |
| jdz-vector decode     | 12ns | 52ns  | 299ns  | 205µs  | 2088µs  | 20.90ms |
| jdz-vector decodeFast | 11ns | 43ns  | 170ns  | 139µs  | 1409µs  | 14.02ms |
| java.util decode      | 14ns | 49ns  | 138ns  | 109µs  | 6727µs  | 66.92ms |
| migBase64 decode      | 30ns | 157ns | 1472ns | 1554µs | 15673µs | 171.8ms |
| migBase64 decodeFast  | 28ns | 104ns | 955ns  | 894µs  | 9000µs  | 98.96ms |

As only 5 warmups were performed, we can see java.util methods cease to be intrinsified for 10MB+ data (see "java.util Intrinsification").

### RFC2045 (MIME)

#### RFC2045 Encoding
| Method/Unencoded Length | 10B  | 100B | 1KB   | 1MB   | 10MB   | 100MB   |
|-------------------|------|------|-------|-------|--------|---------|
| jdz-scalar encode | 10ns | 50ns | 349ns | 358µs | 3799us | 39.80ms |
| jdz-vector encode | 10ns | 38ns | 179ns | 169µs | 1887µs | 20.78ms |
| java.util encode  | 12ns | 28ns | 181ns | 171µs | 1767µs | 17.30ms |
| migBase64 encode  | 16ns | 72ns | 644ns | 620µs | 6239µs | 62.39ms |

#### RFC2045 Decoding
| Method/Unencoded Length    | 10B   | 100B  | 1KB     | 1MB    | 10MB    | 100MB |
|-----------------------|-------|-------|---------|--------|---------|-------|
| jdz-scalar decode     | 29ns  | 247ns | 2.64µs  | 1608µs | 16.12ms | 133.60ms |
| jdz-scalar decodeFast | 22ns  | 49ns  | 376ns   | 358µs  | 3.569ms  | 36.06ms |
| jdz-vector decode     | 29ns  | 61ns  | 575ns   | 564µs  | 5.698ms  | 57.258ms |
| jdz-vector decodeFast | 23ns  | 38ns  | 167ns   | 151µs  | 1.528ms  | 14.77ms |
| java.util decode      | 29ns  | 132ns | 1284ns | 1087µs | 10.92ms | 135.35ms |
| migBase64 decode      | 408ns | 306ns | 2.89µs  | 2383µs | 24.11ms | 286.59ms |
| migBase64 decodeFast  | 29ns  | 105ns | 987ns   | 1052µs | 10.72ms | 125.53ms |

