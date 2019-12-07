package Peer.Common;

public enum Command {
    DownloadTextFile,
    UploadTextFile,
    DownloadBinaryFile,
    UploadBinaryFile,
    GetAvailableFiles;


    public byte getBytes(){
        return (byte)ordinal();
    }

    public static Command fromBytes(byte command){
        return Command.values()[command];
    }

    public Command getOpposite(){
        switch (this){
            case DownloadTextFile: return UploadTextFile;
            case UploadTextFile: return DownloadTextFile;
            case DownloadBinaryFile: return UploadBinaryFile;
            case UploadBinaryFile: return DownloadBinaryFile;
        }
        return null;
    }
}