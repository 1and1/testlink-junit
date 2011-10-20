/**
 * Copyright 2011 Mirko Friedenhagen 
 */

package net.oneandone.testlinkjunit.tljunit;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;

/**
 * {@link TestLinkStrategy} which logs events to the injected logger.
 */
final class InTestLinkLogStrategy extends AbstractInTestLinkStrategy {

    /** logger to logger to. */
    private final Logger logger;

    /**
     * @param logger to logger to.
     */
    InTestLinkLogStrategy(final Logger log) {
        this.logger = log;
    }

    /** {@inheritDoc} */
    @Override
    public void setPassedWhenNoFailure(Description description) {
        if (hasPassed()) {
            logger.info("END Testcase '{}' {} PASSED", getId(description), description.getDisplayName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setFailed(Failure failure) {
        setCurrentFailure(failure);
        logger.error("END Testcase '{}' '{}' FAILED because '{}'.", new Object[] { getId(failure.getDescription()),
                failure.getTestHeader(), failure.getMessage() });
    }

    /** {@inheritDoc} */
    @Override
    public void setBlockedWhenIgnored(Description description) {
        final String message = description.getAnnotation(Ignore.class).value();
        logger.warn("END Testcase '{}' '{}' BLOCKED because '{}'.",
                new Object[] { getId(description), description.getDisplayName(), message });
    }

    /** {@inheritDoc} */
    @Override
    public void setBlockedWhenAssumptionFailed(Failure failure) {
        setCurrentFailure(failure);
        logger.warn("END Testcase '{}' '{}' BLOCKED because '{}'.", new Object[] { getId(failure.getDescription()),
                failure.getTestHeader(), failure.getMessage() });

    }

    /** {@inheritDoc} */
    @Override
    public void addNewTestCase(Description description) {
        setCurrentFailure(NO_FAILURE);
        logger.info("START Testcase '{}' '{}'.", getId(description), description.getDisplayName());
    }

    /**
     * Returns the {@link TestLinkId} from the {@link TestLink} annotation of the description.
     *
     * @param description
     *            to exctract the id from.
     * @return the {@link TestLinkId} from the {@link TestLink} annotation of the description.
     */
    String getId(Description description) {
        return String.valueOf(TestLinkId.fromDescription(description));
    }

}
