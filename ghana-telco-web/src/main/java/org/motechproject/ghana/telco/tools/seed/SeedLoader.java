package org.motechproject.ghana.telco.tools.seed;

import org.apache.log4j.Logger;

import java.util.List;

public class SeedLoader {
    private static Logger LOG = Logger.getLogger(SeedLoader.class);
    private final List<? extends Seed> seeds;

    public SeedLoader(List<? extends Seed> seeds) {
        this.seeds = seeds;
    }

    public void load() {
        LOG.info("Started loading seeds :" + seeds.toString());
        for (Seed seed : seeds) {
            seed.run();
        }
    }
}
