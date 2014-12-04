/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.client;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.compute.*;
import org.gridgain.grid.*;
import org.gridgain.grid.resources.*;
import org.gridgain.grid.util.typedef.internal.*;

import java.util.*;

import static org.apache.ignite.compute.ComputeJobResultPolicy.*;

/**
 * Get affinity for task argument.
 */
public class GridClientGetAffinityTask extends GridTaskSingleJobSplitAdapter<String, Integer> {
    /** Grid. */
    @GridInstanceResource
    private transient Ignite ignite;

    /** {@inheritDoc} */
    @Override protected Object executeJob(int gridSize, String arg) throws GridException {
        A.notNull(arg, "task argument");

        String[] split = arg.split(":", 2);

        A.ensure(split.length == 2, "Task argument should have format 'cacheName:affinityKey'.");

        String cacheName = split[0];
        String affKey = split[1];

        if ("null".equals(cacheName))
            cacheName = null;

        ClusterNode node = ignite.cluster().mapKeyToNode(cacheName, affKey);

        return node.id().toString();
    }

    /** {@inheritDoc} */
    @Override public ComputeJobResultPolicy result(ComputeJobResult res, List<ComputeJobResult> rcvd) throws GridException {
        if (res.getException() != null)
            return FAILOVER;

        return WAIT;
    }
}
