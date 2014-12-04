package org.gridgain.client;

import org.apache.ignite.compute.*;
import org.gridgain.grid.*;

import java.util.*;

import static org.apache.ignite.compute.ComputeJobResultPolicy.*;

/**
 * Test task, that sleeps for 10 seconds in split and returns
 * the length of an argument.
 */
public class GridSleepTestTask extends GridComputeTaskSplitAdapter<String, Integer> {
    /** {@inheritDoc} */
    @Override public Collection<? extends ComputeJob> split(int gridSize, String arg)
        throws GridException {
        return Collections.singleton(new ComputeJobAdapter(arg) {
            @Override public Object execute() {
                try {
                    Thread.sleep(10000);

                    String val = argument(0);

                    return val == null ? 0 : val.length();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override public Integer reduce(List<ComputeJobResult> results) throws GridException {
        int sum = 0;

        for (ComputeJobResult res : results)
            sum += res.<Integer>getData();

        return sum;
    }

    /** {@inheritDoc} */
    @Override public ComputeJobResultPolicy result(ComputeJobResult res, List<ComputeJobResult> rcvd) throws GridException {
        if (res.getException() != null)
            return FAILOVER;

        return WAIT;
    }
}
