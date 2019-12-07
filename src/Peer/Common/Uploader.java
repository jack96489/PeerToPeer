package Peer.Common;

import java.io.Closeable;
import java.io.IOException;

public interface Uploader extends Closeable {

    void uploadFile(String fileName) throws IOException;
}
