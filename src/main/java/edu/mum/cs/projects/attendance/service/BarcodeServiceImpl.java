package edu.mum.cs.projects.attendance.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.mum.cs.projects.attendance.domain.entity.BarcodeRecord;
import edu.mum.cs.projects.attendance.domain.entity.Location;
import edu.mum.cs.projects.attendance.domain.entity.Timeslot;
import edu.mum.cs.projects.attendance.repository.DataAccessFacade;
import edu.mum.cs.projects.attendance.util.DateUtil;

@Service
public class BarcodeServiceImpl implements BarcodeService {
	
	@Value("${barcode.records.file.path}")
	private String filePath;
	
	@Autowired
	private DataAccessFacade dataAccess;
	
	private static volatile Collection<BarcodeRecord> barcodeRecords;
	
	@Override
	@Transactional
	public List<BarcodeRecord> getBarcodeRecordsList() {
		// Default is all dates
		return getBarcodeRecordsList(LocalDate.ofEpochDay(0), LocalDate.now());
	}
	
	@Override
	@Transactional
	public List<BarcodeRecord> getBarcodeRecordsList(LocalDate startDate, LocalDate endDate) {
		Comparator<BarcodeRecord> byDate = (b1, b2) -> b1.getDate().compareTo(b2.getDate());
		Comparator<BarcodeRecord> byTimeslot = (b1, b2) -> b1.getTimeslot().getId().compareTo(b2.getTimeslot().getId());
		Comparator<BarcodeRecord> byBarcode = (b1, b2) -> b1.getBarcode().compareTo(b2.getBarcode());
		
		// The dates are decremented/incremented to make the date range inclusive
		return getBarcodeRecords()
					.stream()
					.filter(b -> b.getDate().isAfter(startDate.minusDays(1)))
					.filter(b -> b.getDate().isBefore(endDate.plusDays(1)))
					.sorted(byDate.thenComparing(byTimeslot).thenComparing(byBarcode))
					.collect(Collectors.toList());
	}
	
	private Collection<BarcodeRecord> getBarcodeRecords() {
		if(null == barcodeRecords) {
			barcodeRecords = loadBarcodeRecords();
		}
		
		return barcodeRecords;
	}
	
	private synchronized Collection<BarcodeRecord> loadBarcodeRecords() {
		if(null != barcodeRecords) {
			return barcodeRecords;
		}
		
		System.out.println("Loading scanned barcode records...");
		
		File file = new File(filePath);
		long fileSize = file.length();

		Map<String, BarcodeRecord> dataMap = new HashMap<String, BarcodeRecord>((int)(fileSize/20));
		
		try {
			Scanner sc = new Scanner(file);
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				
				if(line.isEmpty()) {
					continue;
				}
						
				// Removes duplicates
				if(dataMap.containsKey(line)) {
					System.out.println("Duplicate line found in scanned barcode import: " + line);
					continue;
				}
				else {
					dataMap.put(line,  convertLineToBarcodeRecord(line));
				}
			}			
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return dataMap.values();
	}
	
	private BarcodeRecord convertLineToBarcodeRecord(String line) {
		String[] parts = line.split(",");
		
		String barcode = parts[0];		
		LocalDate date = DateUtil.convertDateToLocalDate(DateUtil.convertOldFormatStringToDate(parts[1]));
		LocalTime time = LocalTime.of(00, 00);
		Timeslot timeslot = dataAccess.findTimeslotById(parts[2]);
		Location location = dataAccess.findLocationById(parts[3]);
		
		return new BarcodeRecord(barcode, date, time, timeslot, location);
	}
}
