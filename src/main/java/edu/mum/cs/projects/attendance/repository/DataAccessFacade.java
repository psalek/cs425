package edu.mum.cs.projects.attendance.repository;

import java.util.List;

import edu.mum.cs.projects.attendance.domain.entity.AcademicBlock;
import edu.mum.cs.projects.attendance.domain.entity.BarcodeRecord;
import edu.mum.cs.projects.attendance.domain.entity.Course;
import edu.mum.cs.projects.attendance.domain.entity.CourseOffering;
import edu.mum.cs.projects.attendance.domain.entity.Location;
import edu.mum.cs.projects.attendance.domain.entity.Student;
import edu.mum.cs.projects.attendance.domain.entity.Timeslot;

public interface DataAccessFacade {

	List<BarcodeRecord> findMeditationRecords();

	List<BarcodeRecord> findMeditationRecords(String entryDate);

	List<BarcodeRecord> findMeditationRecords(AcademicBlock block);

	Timeslot findTimeslotById(String id);

	Location findLocationById(String id);

	List<CourseOffering> findCourseOfferingByCourse(Course course);

	List<CourseOffering> findCourseOfferingByStartDate(String startDate);

	AcademicBlock finAcademicBlockByBeginDate(String blockBeginDate);

	List<Student> findStudentsByEntryDate(String entryDate);

}