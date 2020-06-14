package fr.an.tests.hivemetastorejpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import fr.an.tests.hivemetastorejpa.domain.MTable;
import fr.an.tests.hivemetastorejpa.repo.MTableRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class HiveMetastoreAppMain {

	public static void main(String[] args) {
		SpringApplication.run(HiveMetastoreAppMain.class, args);
	}
}

@Slf4j
@Component
class HiveMetastoreAppCmdLineRunner implements CommandLineRunner {

	@Autowired private MTableRepository tableRepo;
	
	@Override
	public void run(String... args) throws Exception {
		List<MTable> tables = tableRepo.findAll();
		log.info("tables: " + tables.size());
	}
	
}