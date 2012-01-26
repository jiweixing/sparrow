package edu.berkeley.sparrow.daemon.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration.Configuration;
import org.mortbay.log.Log;

import com.google.common.base.Optional;

import edu.berkeley.sparrow.daemon.SparrowConf;
import edu.berkeley.sparrow.thrift.TResourceVector;

/**
 * Utilities to aid the configuration file-based scheduler and node monitor.
 */
public class ConfigUtil {
  /**
   * Parses the list of backends from a {@code Configuration}.
   */
  public static ConcurrentMap<InetSocketAddress, TResourceVector> parseBackends(
      Configuration conf) {
    if (!conf.containsKey(SparrowConf.STATIC_BACKENDS) || 
        !conf.containsKey(SparrowConf.STATIC_MEM_PER_BACKEND)) {
      throw new RuntimeException("Missing configuration backend list.");
    }
    
    ConcurrentMap<InetSocketAddress, TResourceVector> backends =
        new ConcurrentHashMap<InetSocketAddress, TResourceVector>();
    TResourceVector nodeResources = TResources.createResourceVector(
        conf.getInt(SparrowConf.STATIC_MEM_PER_BACKEND));
    
    for (String node: conf.getStringArray(SparrowConf.STATIC_BACKENDS)) {
      Optional<InetSocketAddress> addr = Serialization.strToSocket(node);
      if (!addr.isPresent()) {
        Log.warn("Bad backend address: " + node);
        continue;
      }
      backends.put(addr.get(), nodeResources);
    }
    
    return backends;
  }
  
  /**
   * Parses a list of schedulers from a {@code Configuration}
   * @return
   */
  public static List<InetSocketAddress> parseSchedulers(Configuration conf) {
    if (!conf.containsKey(SparrowConf.STATIC_SCHEDULERS)) {
      throw new RuntimeException("Missing configuration frontend list.");
    }
    List<InetSocketAddress> frontends = new ArrayList<InetSocketAddress>();
    for (String node: conf.getStringArray(SparrowConf.STATIC_SCHEDULERS)) {
      Optional<InetSocketAddress> addr = Serialization.strToSocket(node);
      if (!addr.isPresent()) {
        Log.warn("Bad scheduler address: " + node);
        continue;
      }
      frontends.add(addr.get());
    }
    return frontends;
  }
}
