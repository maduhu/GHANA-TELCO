package org.motechproject.ghana.mtn.tools.seed;

import org.apache.log4j.Logger;

public abstract class Seed {
    Logger LOG = Logger.getLogger(this.getClass());

    public void run() {
        preLoad();
        load();
        postLoad();
    }

    private void preLoad() {
        LOG.info("loading: START");
    }

    private void postLoad() {
        LOG.info("loading: END");
    }

    protected abstract void load();

}