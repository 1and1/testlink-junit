/**
 * Copyright 2011 Mirko Friedenhagen 
 */

package net.oneandone.testlinkjunit.tljunit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * @author mirko
 * 
 */
public class ThreadingTest extends AbstractTestLinkRunListenerTest {

    private final ExecutorService pool = Executors.newFixedThreadPool(50);

    private final TestLinkRunListener listener;

    private final PrintStream out;

    public ThreadingTest() throws FileNotFoundException {
        out = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // just do nothing
            }
        });
        listener = new TestLinkRunListener(out, "donald");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        pool.shutdown();
        pool.awaitTermination(100, TimeUnit.SECONDS);
        final Xpp3Dom results = listener.getResults();
        assertEquals(600, results.getChildCount());
        assertEquals(400, checkResultsAndCountTestCasesWithExternalId(results));
        assertEquals(100, getIgnoredTestCases(results));
        final PrintStream stream = new PrintStream(new File("target/parallel-testlink.xml"));
        try {
            stream.print(results.toString());
        } finally {
            stream.close();
        }
    }

    @TestLink(externalId = "testParallel")
    @Test
    public void testParallel() throws FileNotFoundException {
        for (int i = 0; i < 100; i++) {
            final JUnitCore core = new JUnitCore();
            core.addListener(listener);
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    final Result result = core.run(SUTTestLinkRunListener.class);
                    assertEquals(7, result.getRunCount());
                    assertEquals(2, result.getIgnoreCount());
                }
            });
        }
    }
}
