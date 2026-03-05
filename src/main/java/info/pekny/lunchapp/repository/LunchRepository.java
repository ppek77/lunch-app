package info.pekny.lunchapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import info.pekny.lunchapp.entity.Lunch;

public interface LunchRepository extends JpaRepository<Lunch, Long> {

	List<Lunch> findAllByOrderByDateDesc();
}
