package edu.mum.cs.projects.attendance.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.mum.cs.projects.attendance.domain.entity.AcademicBlock;
import edu.mum.cs.projects.attendance.domain.entity.BarcodeRecord;
import edu.mum.cs.projects.attendance.domain.entity.Course;
import edu.mum.cs.projects.attendance.domain.entity.CourseOffering;
import edu.mum.cs.projects.attendance.domain.entity.Location;
import edu.mum.cs.projects.attendance.domain.entity.Student;
import edu.mum.cs.projects.attendance.domain.entity.Timeslot;
import edu.mum.cs.projects.attendance.util.DateUtil;

@Repository
public class DataAccessFacadeImpl implements DataAccessFacade {
	
	@Autowired
	private AcademicBlockRepository academicBlockRepository;

	@Autowired
	private BarcodeRecordRepository barcodeRecordRepository;

	@Autowired
	private CourseOfferingRepository courseOfferingRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TimeslotRepository timeslotRepository;

	@Override
	public List<BarcodeRecord> findMeditationRecords() {
		List<BarcodeRecord> records = new LinkedList<>();
		barcodeRecordRepository.findAll().forEach(br -> records.add(br));
		
		return Collections.unmodifiableList(records);
	}

	@Override
	public List<BarcodeRecord> findMeditationRecords(String entryDate) {
		Date endDate = new Date();
		Date beginDate = DateUtil.convertStringToDate(entryDate);
		List<BarcodeRecord> records = barcodeRecordRepository.findByDateBetween(beginDate, endDate);

		return records.stream()
				.filter(b -> b.getLocation().equals("DB"))
				.filter(b -> b.getTimeslot().equals("AM"))
				.sorted(Comparator.comparing(BarcodeRecord::getDate))
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<BarcodeRecord> findMeditationRecords(AcademicBlock block) {
		Date beginDate = DateUtil.convertLocalDateToDate(block.getBeginDate());
		Date endDate = DateUtil.convertLocalDateToDate(block.getEndDate());
		
		return barcodeRecordRepository.findByDateBetween(beginDate, endDate);
	}

	@Override
	public Timeslot findTimeslotById(String id) {
		return timeslotRepository.findOne(id);
	}

	@Override
	public Location findLocationById(String id) {
		return locationRepository.findOne(id);
	}

	@Override
	public List<CourseOffering> findCourseOfferingByCourse(Course course) {
		return courseOfferingRepository.findByCourse(course);
	}

	@Override
	public List<CourseOffering> findCourseOfferingByStartDate(String startDate) {
		Date date = DateUtil.convertStringToDate(startDate);
		
		AcademicBlock block = academicBlockRepository.findByBeginDate(date);
		
		return courseOfferingRepository.findByStartDate(date).stream()
				.filter(o -> o.isOnCampus() && o.isActive())
				.peek(o -> o.setBlock(block))
				.collect(Collectors.toList());
	}

	@Override
	public AcademicBlock finAcademicBlockByBeginDate(String blockBeginDate) {
		Date date = DateUtil.convertStringToDate(blockBeginDate);
		
		return academicBlockRepository.findByBeginDate(date);
	}

	@Override
	public List<Student> findStudentsByEntryDate(String entryDate) {
		return studentRepository.findByEntryDate(DateUtil.convertStringToDate(entryDate));
	}

}
