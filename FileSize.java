package com.ucr.cs172.project.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

public class FileSize {
	public boolean fileSize() {
		long size = FileUtils.sizeOfDirectory(new File("storage/"));
		double GB = 1073741824;
		if(size >= 5*GB) return false; // 5 GB to Bytes = 5368709120
		return true;
	}
}
