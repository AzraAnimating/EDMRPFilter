/*
 * Copyright (c) 2020. Tobias Rempe
 * This File, its contents and by extention the corresponding project is property of Tobias Rempe and may not be used without explicit permission to do so.
 *
 * tobiasrempe@zyonicsoftware.com
 */

package de.azraanimating.edmrpfilter.reader;

import de.azraanimating.edmrpfilter.filter.Detective;
import org.json.JSONException;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class EddnReader extends Thread {

    public static final String SCHEMA_KEY = "$schemaRef";
    public static final String RELAY = "tcp://eddn.edcd.io:9500";
    private final Detective detective;

    public EddnReader(final Detective detective) {
        this.detective = detective;
    }

    public void msg(final String msg) {
        System.out.println(msg);
    }


    public synchronized void readStream() {

        final ZContext ctx = new ZContext();
        final ZMQ.Socket client = ctx.createSocket(ZMQ.SUB);
        client.subscribe("".getBytes());
        client.setReceiveTimeOut(30000);

        client.connect(EddnReader.RELAY);
        this.msg("EDDN Relay connected");
        final ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(client, ZMQ.Poller.POLLIN);
        final byte[] output = new byte[256 * 1024];
        while (true) {
            final int poll = poller.poll(10);
            if (poll == ZMQ.Poller.POLLIN) {
                final ZMQ.PollItem item = poller.getItem(poll);

                if (poller.pollin(0)) {
                    final byte[] recv = client.recv(ZMQ.NOBLOCK);
                    if (recv.length > 0) {
                        // decompress
                        final Inflater inflater = new Inflater();
                        inflater.setInput(recv);
                        try {
                            final int outlen = inflater.inflate(output);
                            final String outputString = new String(output, 0, outlen, StandardCharsets.UTF_8);
                            // outputString contains a json message

                            if (outputString.contains(EddnReader.SCHEMA_KEY)) {
                                //msg(outputString);
                                this.detective.rawFilter(outputString);
                            }

                        } catch (final DataFormatException | IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
