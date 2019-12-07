package Peer.Common;

import java.io.Closeable;
import java.io.IOException;

public interface Downloader extends Closeable{

    void downloadFile(String fileName) throws IOException;
}
