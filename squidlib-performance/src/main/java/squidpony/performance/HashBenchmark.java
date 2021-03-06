/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package squidpony.performance;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import squidpony.squidmath.CrossHash;
import squidpony.squidmath.LongPeriodRNG;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * HashBenchmark.measureFNV           avgt    3  138.403 ± 13.317  ms/op
 * HashBenchmark.measureFNVInt        avgt    3  150.326 ± 21.640  ms/op
 * HashBenchmark.measureJVMInt        avgt    3   15.855 ± 16.418  ms/op
 * HashBenchmark.measureLightning     avgt    3   21.636 ±  4.122  ms/op
 * HashBenchmark.measureLightningInt  avgt    3   20.268 ±  0.624  ms/op
 * HashBenchmark.measureSip           avgt    3   78.582 ± 25.881  ms/op
 * HashBenchmark.measureSipInt        avgt    3   79.385 ± 30.757  ms/op
 *
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * HashBenchmark.measureFNV           avgt    3  137.832 ± 14.774  ms/op
 * HashBenchmark.measureFNVInt        avgt    3  150.722 ±  5.292  ms/op
 * HashBenchmark.measureJVMInt        avgt    3   14.928 ±  1.250  ms/op
 * HashBenchmark.measureLightning     avgt    3   16.862 ±  6.673  ms/op
 * HashBenchmark.measureLightningInt  avgt    3   18.505 ±  0.817  ms/op
 * HashBenchmark.measureSip           avgt    3   77.700 ±  6.883  ms/op
 * HashBenchmark.measureSipInt        avgt    3   77.576 ±  2.672  ms/op
 *
 * Benchmark                          Mode  Cnt    Score    Error  Units
 * HashBenchmark.measureFNV           avgt    3  137.748 ± 31.812  ms/op
 * HashBenchmark.measureFNVInt        avgt    3  150.993 ± 19.462  ms/op
 * HashBenchmark.measureJVMInt        avgt    3   15.003 ±  2.244  ms/op
 * HashBenchmark.measureLightning     avgt    3   19.766 ±  3.597  ms/op
 * HashBenchmark.measureLightningInt  avgt    3   19.550 ±  7.034  ms/op
 * HashBenchmark.measureSip           avgt    3   78.889 ± 21.236  ms/op
 * HashBenchmark.measureSipInt        avgt    3   77.797 ±  5.196  ms/op
 * HashBenchmark.measureStorm         avgt    3   24.542 ±  2.893  ms/op
 * HashBenchmark.measureStormInt      avgt    3   25.070 ±  3.274  ms/op
 *
 * Benchmark                          Mode  Cnt    Score   Error  Units
 * HashBenchmark.measureControl       avgt    8    1.980 ± 0.015  ms/op
 * HashBenchmark.measureFNV           avgt    8  136.929 ± 1.341  ms/op
 * HashBenchmark.measureFNVInt        avgt    8  150.062 ± 1.248  ms/op
 * HashBenchmark.measureFalcon        avgt    8   15.653 ± 0.249  ms/op
 * HashBenchmark.measureFalconInt     avgt    8   14.999 ± 0.199  ms/op <-- This is important!
 * HashBenchmark.measureJVMInt        avgt    8   15.030 ± 0.111  ms/op <-- Because this is the collision-prone default!
 * HashBenchmark.measureLightning     avgt    8   19.643 ± 0.109  ms/op
 * HashBenchmark.measureLightningInt  avgt    8   19.332 ± 0.154  ms/op
 * HashBenchmark.measureStorm         avgt    8   24.422 ± 0.185  ms/op
 * HashBenchmark.measureStormInt      avgt    8   25.002 ± 0.306  ms/op
 */
public class HashBenchmark {

    private static long seed = 9000;
    private static int iseed = 9000;

    public long doFNV()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            seed += CrossHash.hash64(rng.state);
        }
        return seed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureFNV() throws InterruptedException {
        seed = 9000;
        doFNV();
    }

    public long doFNVInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += CrossHash.hash(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureFNVInt() throws InterruptedException {
        iseed = 9000;
        doFNVInt();
    }

    public long doLightning()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            seed += CrossHash.Lightning.hash64(rng.state);
        }
        return seed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureLightning() throws InterruptedException {
        seed = 9000;
        doLightning();
    }

    public long doLightningInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += CrossHash.Lightning.hash(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureLightningInt() throws InterruptedException {
        iseed = 9000;
        doLightningInt();
    }

    public long doJVMInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += Arrays.hashCode(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureJVMInt() throws InterruptedException {
        iseed = 9000;
        doJVMInt();
    }
/*
    public long doStorm()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);
        CrossHash.Storm storm = new CrossHash.Storm();
        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            seed += storm.hash64(rng.state);
        }
        return seed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureStorm() throws InterruptedException {
        seed = 9000;
        doStorm();
    }

    public long doStormInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);
        CrossHash.Storm storm = new CrossHash.Storm();
        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += storm.hash(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureStormInt() throws InterruptedException {
        iseed = 9000;
        doStormInt();
    }
    */
    public long doStorm()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);
        CrossHash.Storm storm = CrossHash.Storm.chi;
        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            seed += storm.hash64(rng.state);
        }
        return seed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureStorm() throws InterruptedException {
        seed = 9000;
        doStorm();
    }

    public long doStormInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);
        CrossHash.Storm storm = CrossHash.Storm.chi;
        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += storm.hash(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureStormInt() throws InterruptedException {
        iseed = 9000;
        doStormInt();
    }


    public long doFalcon()
    {
        LongPeriodRNG rng = new LongPeriodRNG(seed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            seed += CrossHash.Falcon.hash64(rng.state);
        }
        return seed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureFalcon() throws InterruptedException {
        seed = 9000;
        doFalcon();
    }

    public long doFalconInt()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);

        for (int i = 0; i < 1000000; i++) {
            rng.nextLong();
            iseed += CrossHash.Falcon.hash(rng.state);
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureFalconInt() throws InterruptedException {
        iseed = 9000;
        doFalconInt();
    }

    public long doControl()
    {
        LongPeriodRNG rng = new LongPeriodRNG(iseed);

        for (int i = 0; i < 1000000; i++) {
            iseed += rng.nextLong();
        }
        return iseed;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureControl() throws InterruptedException {
        iseed = 9000;
        doControl();
    }



    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the different run modes for the same benchmark.
     * Note the units are different, scores are consistent with each other.
     *
     * You can run this test:
     *
     * a) Via the command line from the squidlib-performance module's root folder:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar HashBenchmark -wi 3 -i 3 -f 1
     *
     *    (we requested 5 warmup/measurement iterations, single fork)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashBenchmark.class.getSimpleName())
                .timeout(TimeValue.seconds(60))
                .warmupIterations(8)
                .measurementIterations(8)
                .forks(1)
                .build();

        new Runner(opt).run();
    }


}
