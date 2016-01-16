package org.bubble.cloud.speech;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import org.apache.log4j.xml.DOMConfigurator;
import org.bubblecloud.speech.nlpapi.NanoHttpdJsonRpcServer;
import org.bubblecloud.speech.nlpapi.NanoHttpdJsonRpcServerResponse;
import org.bubblecloud.speech.nlpapi.SpeechNlpApi;
import org.bubblecloud.speech.nlpapi.SpeechNlpApiImpl;

/**
 * Speech NPL JSON RPC HTTP server.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SpeechNlpHerokuMain extends NanoHTTPD {
    /**
     * The logger.
     */
    final static Logger LOGGER = Logger.getLogger("org.bubble.cloud.speech");
    /**
     * The JSON ROC server.
     */
    private final NanoHttpdJsonRpcServer jsonRpcServer;

    public static void main(String[] args) {
        try {
            DOMConfigurator.configure("log4j.xml");
            new SpeechNlpHerokuMain();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting Speech NLP JSON RPC HTTP server.", e);
        }
    }

    public SpeechNlpHerokuMain() throws IOException {
        super(Integer.valueOf(System.getenv("PORT")));
        final SpeechNlpApi api = new SpeechNlpApiImpl();
        jsonRpcServer = new NanoHttpdJsonRpcServer(api, SpeechNlpApi.class);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        LOGGER.info("Speech NLP JSON RPC HTTP server started.");
    }

    @Override
    public Response serve(IHTTPSession session) {
        final NanoHttpdJsonRpcServerResponse response = jsonRpcServer.handle(session);
        return newFixedLengthResponse(response.getStatus(), "application/json-rpc", response.getMessage());
    }

}