package com.flyn.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPFile;

public class ApacheFtpUploadHandler extends ApacheFtpHandler
{

    protected ApacheFtpUploadHandler(FtpRequest ftpRequest, FtpResponseListener ftpResponseHandler)
    {
        super(ftpRequest, ftpResponseHandler);
    }

    @Override
    protected void doTask() throws CustomFtpExcetion
    {
        File localFile = new File(this.ftpRequest.getLocalFilePath());
        if (!localFile.exists() || localFile.length() <= 0)
            throw new CustomFtpExcetion("LocalFile not found.");

        String remoteDirectory = this.ftpRequest.getRemoteFilePath().substring(0, this.ftpRequest.getRemoteFilePath().lastIndexOf("/"));
        createDirectory(remoteDirectory, null);
        changeWorkingDirectory(remoteDirectory, null);

        FTPFile ftpFile = getRemoteFile(this.ftpRequest.getRemoteFilePath(), null);
        if (null != ftpFile && ftpFile.getSize() >= localFile.length())
            throw new CustomFtpExcetion("RemoteFile exists.");

        boolean result = false;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try
        {
            this.bytesTotal = (int) localFile.length();
            inputStream = new BufferedInputStream(new FileInputStream(localFile));
            if (null != ftpFile && ftpFile.getSize() < localFile.length())
            {

                this.bytesWritten = (int) ftpFile.getSize();
                outputStream = new BufferedOutputStream(this.ftpClient.appendFileStream(this.ftpRequest.getRemoteFilePath()));
                inputStream.skip(ftpFile.getSize());
                this.ftpClient.setRestartOffset(ftpFile.getSize());

            } else
            {
                outputStream = new BufferedOutputStream(this.ftpClient.storeFileStream(this.ftpRequest.getRemoteFilePath()));
            }

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int count;
            while (!isCancelled() && (count = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, count);
                updateProgress(count);
            }
            outputStream.flush();

        } catch (FileNotFoundException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (NullPointerException e)
        {
            throw new CustomFtpExcetion(e);
        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        } finally
        {
            IFtpHandler.closeQuickly(inputStream);
            IFtpHandler.closeQuickly(outputStream);
        }
        try
        {
            result = this.ftpClient.completePendingCommand();

        } catch (IOException e)
        {
            throw new CustomFtpExcetion(e);
        }
        if (!result)
            throw new CustomFtpExcetion("Upload file to ftp failed");

    }

}
