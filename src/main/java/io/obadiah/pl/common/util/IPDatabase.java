package io.obadiah.pl.common.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import java.io.*;
import java.net.*;
import java.util.zip.GZIPInputStream;

public class IPDatabase {

    private final File databaseFile;
    private DatabaseReader reader;

    public IPDatabase(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    public void downloadDatabase(String url, String licenseKey) throws RuntimeException{
        try {
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("Url is empty");
            }

            if (licenseKey == null || licenseKey.isEmpty()) {
                throw new RuntimeException("No license key");
            }

            url = url.replace("{LICENSEKEY}", licenseKey);
            System.out.println("Downloading IP database");
            final URL downloadUrl = new URL(url);
            final URLConnection conn = downloadUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.connect();
            InputStream input = conn.getInputStream();
            final OutputStream output = new FileOutputStream(databaseFile);
            final byte[] buffer = new byte[2048];
            if (url.contains("gz")) {
                input = new GZIPInputStream(input);
                if (url.contains("tar.gz")) {
                    // The new GeoIP2 uses tar.gz to pack the db file along with some other txt. So it makes things a bit complicated here.
                    String filename;
                    final TarInputStream tarInputStream = new TarInputStream(input);
                    TarEntry entry;
                    while ((entry = tarInputStream.getNextEntry()) != null) {
                        if (!entry.isDirectory()) {
                            filename = entry.getName();
                            if (filename.substring(filename.length() - 5).equalsIgnoreCase(".mmdb")) {
                                input = tarInputStream;
                                break;
                            }
                        }
                    }
                }
            }
            int length = input.read(buffer);
            while (length >= 0) {
                output.write(buffer, 0, length);
                length = input.read(buffer);
            }
            output.close();
            input.close();
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void load() {
        if (!this.databaseFile.exists())
            throw new IllegalStateException(this.databaseFile.getName() + " does not exist so the ipdatabase cannot be loaded");

        try {
            this.reader = new DatabaseReader.Builder(databaseFile).build();
        } catch (Exception e) {
            this.reader = null;
            e.printStackTrace();
        }
    }

    public String getCountryFromIP(String ip) {
        if (this.reader == null)
            return "Unknown";
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address.getHostName().equals("127.0.0.1") || address.isAnyLocalAddress() || address.isLoopbackAddress())
                return "LocalHost";
            return this.reader.country(address).getCountry().getName();
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

}
