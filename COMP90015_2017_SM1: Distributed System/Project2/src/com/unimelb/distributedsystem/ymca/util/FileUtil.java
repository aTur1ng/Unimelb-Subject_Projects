package com.unimelb.distributedsystem.ymca.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.unimelb.distributedsystem.ymca.model.Resource;

public class FileUtil
{
	public static byte[] readBytes(Resource resource) throws IOException
	{
		URI fileLocation = resource.getUri();
		LoggerUtil.println("reading bytes from URI: " + fileLocation.getPath());
		LoggerUtil.println("reading bytes from URI: " + fileLocation.getSchemeSpecificPart());
		File f = new File(fileLocation.getRawSchemeSpecificPart());
		LoggerUtil.println("reading bytes from file: " + f.getAbsolutePath());
		Path path = Paths.get(fileLocation.getSchemeSpecificPart());
		try
		{
			return Files.readAllBytes(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new IOException("invalid resourceTemplate");
		}
	}

	public static void writeBytes(Resource resource, byte[] rawBytes) throws IOException
	{
		URI fileLocation = resource.getUri();
		Path path = Paths.get(fileLocation.getSchemeSpecificPart());
		String fileName = path.getFileName().toString();
		Path targetFile = Paths.get(fileName);
		Path done = Files.write(targetFile, rawBytes, StandardOpenOption.CREATE);
		LoggerUtil.println("File written to path: " + done.toFile().getAbsolutePath());
	}

	public static void extractFilesFromJar(String jarPath, String targetPath)
	{
		File targetFile = new File(targetPath);
		if (targetFile.exists())
		{
			System.out.println(targetFile.getAbsolutePath() + " exists.");
		}
		else
		{
			System.out.println(targetFile.getAbsolutePath() + " doesn't exists.");
			System.out.println("Extracting from path " + FileUtil.class.getResource(jarPath).getPath());
			try (BufferedInputStream in = new BufferedInputStream(FileUtil.class.getResourceAsStream(jarPath));
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile)))
			{
				byte[] buffer = new byte[32 * 1024];
				int len = -1;
				while ((len = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, len);
					out.flush();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println(targetFile.getAbsolutePath() + " extracted.");

		}
	}
}
