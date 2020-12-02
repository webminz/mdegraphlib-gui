package no.hvl.past.webui.backend.quickRuleEngine;

import java.util.concurrent.ThreadLocalRandom;

public class RandomTimer extends Timer {

    private final long lowerBound;
    private final long upperBound;

    public RandomTimer(RuleEngine engine, long lowerBound, long upperBound) {
        super(engine);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


    @Override
    protected void terminate() {
    }

    @Override
    protected void performStartup() {
        new Thread(() -> {
            while (isActive()) {
                long waitTime = ThreadLocalRandom.current().nextLong(lowerBound, upperBound);
                try {
                    Thread.sleep(waitTime);
                    trigger();
                } catch (InterruptedException e) {
                    System.out.println("Random Timer was interrupted, terminating now");
                    terminate();
                }

            }
        }).start();
    }
}
