package io.github.jaredpetersen.kafkaconnectarangodb.source;

import org.apache.kafka.connect.connector.ConnectorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DatabaseHostMonitorThread extends Thread {
  private final ConnectorContext context;
  private final Long pollInterval;
  private final String host;
  private final CountDownLatch shutdownLatch;
  private Set<String> savedAddresses = null;

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseHostMonitorThread.class);

  public DatabaseHostMonitorThread(ConnectorContext context, long pollInterval, String host) {
    this.context = context;
    this.host = host;
    this.pollInterval = pollInterval;
    this.shutdownLatch = new CountDownLatch(1);
  }

  @Override
  public void run() {
    while (shutdownLatch.getCount() > 0) {
      Set<String> foundAddresses = updateDatabaseAddresses();

      if (this.savedAddresses.containsAll(foundAddresses)) {
        this.context.requestTaskReconfiguration();
      }

      try {
        shutdownLatch.await(pollInterval, TimeUnit.MILLISECONDS);
      } catch (InterruptedException exception) {
        LOG.warn("host monitor interrupted: ", exception);
      }
    }
  }

  private Set<String> updateDatabaseAddresses() {
    InetAddress[] foundAddresses;

    // Get IP addresses for host
    try {
      foundAddresses = InetAddress.getAllByName(this.host);
    } catch (UnknownHostException exception) {
      LOG.error("host does not exist");
      foundAddresses = new InetAddress[]{};
    }

    Set<String> foundAddressesSet = Arrays.stream(foundAddresses)
        .map(InetAddress::getHostAddress)
        .collect(Collectors.toSet());

    return foundAddressesSet;
  }

  public synchronized Set<String> getDatabaseAddresses() {
    return this.savedAddresses;
  }

  public void shutdown() {
    this.shutdownLatch.countDown();
  }
}
