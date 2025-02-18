package com.oneself.utils;

import com.jcraft.jsch.*;
import com.oneself.exception.OneselfFtpUploadException;
import com.oneself.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.utils
 * className FileUploaderUtils
 * description 文件上传/下载工具类
 * version 1.0
 */
@Slf4j
@Component
public class FileUploaderUtils {

    private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    private final FileProperties fileProperties;

    @Autowired
    public FileUploaderUtils(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    /**
     * 保存文件到 FTP 服务器
     *
     * @param fileName      文件名
     * @param fileContent   文件内容
     * @param directoryPath 文件所在目录（相对于FTP服务器根目录）
     */
    public void saveFileToFtp(String fileName, String fileContent, String directoryPath) throws OneselfFtpUploadException {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(fileProperties.getFtp().getHost(), fileProperties.getFtp().getPort());
            ftpClient.login(fileProperties.getFtp().getUser(), fileProperties.getFtp().getPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 切换到指定目录，如果目录不存在则创建
            if (!ftpClient.changeWorkingDirectory(directoryPath)) {
                createDirectories(ftpClient, directoryPath);
                // 再次切换到创建后的目录
                ftpClient.changeWorkingDirectory(directoryPath);
            }

            try (OutputStream outputStream = ftpClient.storeFileStream(fileName);
                 Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

                // 使用缓冲提高写入效率
                try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                    bufferedWriter.write(fileContent);
                }
            }

            ftpClient.logout();
            log.info("文件上传 FTP 服务器成功，路径为: 【{}】", directoryPath);
        } catch (IOException e) {
            log.error("文件上传 FTP 服务器失败，报错信息为: {}", e.getMessage());
            throw new OneselfFtpUploadException("文件上传 FTP 服务器失败，报错信息为", e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("文件上传 FTP 服务器失败，报错信息为: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 创建 FTP 目录
     *
     * @param ftpClient     FTP 客户端
     * @param directoryPath 目录路径
     * @throws IOException
     */
    private void createDirectories(FTPClient ftpClient, String directoryPath) throws IOException {
        // 确保路径是绝对路径
        // 使用系统默认的文件分隔符
        if (!directoryPath.startsWith(File.separator)) {
            directoryPath = File.separator + directoryPath;
        }

        // 将路径分解为各级目录
        String[] dirs = directoryPath.split("/");
        // 从根目录开始构建路径
        StringBuilder path = new StringBuilder("/");
        // 切换到根目录
        ftpClient.changeWorkingDirectory("/");

        // 创建每个目录
        for (String dir : dirs) {
            // 忽略空目录部分
            if (dir.isEmpty()) {
                continue;
            }

            // 构建当前路径
            path.append(dir).append("/");
            String currentPath = path.toString();

            // 尝试进入目录
            if (!ftpClient.changeWorkingDirectory(currentPath)) {
                // 如果目录不存在，则创建目录
                if (ftpClient.makeDirectory(currentPath)) {
                    log.info("目录 【{}】 创建成功.", currentPath);
                } else {
                    throw new IOException("创建目录失败 " + currentPath);
                }
                // 进入新创建的目录
                ftpClient.changeWorkingDirectory(currentPath);
            }
        }
    }

    /**
     * 保存文件到 SFTP 服务器
     *
     * @param fileName      文件名
     * @param fileContent   文件内容
     * @param directoryPath 文件所在目录（相对于SFTP服务器根目录）
     */
    public void saveFileToSftp(String fileName, String fileContent, String directoryPath) throws Exception {
        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(fileProperties.getSftp().getUser(), fileProperties.getSftp().getHost(), fileProperties.getSftp().getPort());
            session.setPassword(fileProperties.getSftp().getPassword());
            session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
            session.connect();

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // 切换到指定目录，如果目录不存在则创建
            changeOrCreateDirectory(sftpChannel, directoryPath);

            // 文件写入
            writeToSftpFile(sftpChannel, fileName, fileContent);

            log.info("文件上传 SFTP 服务器成功，路径为: 【{}】", directoryPath);
        } catch (JSchException | SftpException | IOException e) {
            log.error("文件上传 SFTP 服务器失败，报错信息为: {}", e.getMessage());
            throw new OneselfFtpUploadException("文件上传 SFTP 服务器失败", e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }


    /**
     * 将文件内容写入到 SFTP 服务器中的文件
     *
     * @param sftpChannel SFTP 渠道，用于与 SFTP 服务器进行通信
     * @param fileName   文件名，文件将被上传到 SFTP 服务器上的该路径
     * @param fileContent 文件内容，将写入到指定文件中
     * @throws SftpException 如果 SFTP 操作失败
     * @throws IOException 如果写入文件时发生 I/O 异常
     */
    private void writeToSftpFile(ChannelSftp sftpChannel, String fileName, String fileContent) throws SftpException, IOException {
        // 使用 OutputStream 将文件内容上传到 SFTP 服务器
        try (OutputStream outputStream = sftpChannel.put(fileName);
             Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

            // 使用 BufferedWriter 提高写入效率
            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.write(fileContent);
            }
        }
    }

    /**
     * 切换到指定目录，如果目录不存在则创建该目录
     *
     * @param sftpChannel SFTP 渠道，用于与 SFTP 服务器进行通信
     * @param directoryPath 目录路径，目标目录的相对路径
     * @throws SftpException 如果无法切换目录或创建目录时发生错误
     */
    private void changeOrCreateDirectory(ChannelSftp sftpChannel, String directoryPath) throws SftpException {
        try {
            sftpChannel.cd(directoryPath); // 切换到指定目录
        } catch (SftpException e) {
            createDirectories(sftpChannel, directoryPath); // 创建目录
            sftpChannel.cd(directoryPath); // 再次尝试切换到目录
        }
    }


    /**
     * 创建 SFTP 目录
     *
     * @param sftpChannel   SFTP 渠道
     * @param directoryPath 目录路径
     * @throws SftpException
     */
    private void createDirectories(ChannelSftp sftpChannel, String directoryPath) throws SftpException {
        String[] dirs = directoryPath.split("/");
        StringBuilder path = new StringBuilder();

        for (String dir : dirs) {
            // 忽略空目录
            if (dir.isEmpty()) {
                continue;
            }

            path.append("/").append(dir);
            String currentPath = path.toString();

            try {
                // 尝试进入目录，验证目录是否存在
                sftpChannel.cd(currentPath);
            } catch (SftpException e) {
                // 如果目录不存在，则创建它
                try {
                    sftpChannel.mkdir(currentPath);
                } catch (SftpException mkdirException) {
                    // 处理 mkdir 的异常
                    throw new SftpException(mkdirException.id, "创建目录失败: " + currentPath, mkdirException);
                }
            }
        }
    }

    /**
     * 从 FTP 服务器获取文件数据
     *
     * @param fileName      文件名
     * @param directoryPath 文件所在目录（相对于FTP服务器根目录）
     * @return 文件数据
     */
    public String getFileDataFromFtp(String fileName, String directoryPath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(fileProperties.getFtp().getHost(), fileProperties.getFtp().getPort());
            ftpClient.login(fileProperties.getFtp().getUser(), fileProperties.getFtp().getPassword());

            // 切换到指定目录
            ftpClient.changeWorkingDirectory(directoryPath);

            try (InputStream inputStream = ftpClient.retrieveFileStream(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                ftpClient.logout();
                log.info("从 FTP 目录成功检索到文件数据: 【{}】", directoryPath);

                return stringBuilder.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从 SFTP 服务器获取文件数据
     *
     * @param fileName      文件名
     * @param directoryPath 文件所在目录（相对于SFTP服务器根目录）
     * @return 文件数据
     */
    public String getFileDataFromSftp(String fileName, String directoryPath) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(fileProperties.getSftp().getUser(), fileProperties.getSftp().getHost(), fileProperties.getSftp().getPort());
            session.setPassword(fileProperties.getSftp().getPassword());
            session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
            session.connect();

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // 切换到指定目录
            sftpChannel.cd(directoryPath);

            try (InputStream inputStream = sftpChannel.get(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                    } else {
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append(line);
                }
                sftpChannel.disconnect();
                session.disconnect();
                log.info("从 SFTP 目录成功检索到文件数据: 【{}】", directoryPath);

                return stringBuilder.toString();

            }

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从 FTP 服务器删除文件
     *
     * @param fileName      文件名
     * @param directoryPath 文件所在目录（相对于FTP服务器根目录）
     */
    public void deleteFileFromFtp(String fileName, String directoryPath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(fileProperties.getFtp().getHost(), fileProperties.getFtp().getPort());
            ftpClient.login(fileProperties.getFtp().getUser(), fileProperties.getFtp().getPassword());

            // 切换到指定目录
            ftpClient.changeWorkingDirectory(directoryPath);

            // 删除文件
            boolean deleted = ftpClient.deleteFile(fileName);
            if (deleted) {
                log.info("从 FTP 目录成功删除文件: 【{}】", directoryPath);
            } else {
                log.warn("Failed to delete file from FTP: " + fileName);
            }

            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从 SFTP 服务器删除文件
     *
     * @param fileName      文件名
     * @param directoryPath 文件所在目录（相对于SFTP服务器根目录）
     */
    public void deleteFileFromSftp(String fileName, String directoryPath) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(fileProperties.getSftp().getUser(), fileProperties.getSftp().getHost(), fileProperties.getSftp().getPort());
            session.setPassword(fileProperties.getSftp().getPassword());
            session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
            session.connect();

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // 切换到指定目录
            sftpChannel.cd(directoryPath);

            // 删除文件
            sftpChannel.rm(fileName);
            log.info("从 SFTP 目录删除文件成功: 【{}】", directoryPath);

            sftpChannel.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
    }
}