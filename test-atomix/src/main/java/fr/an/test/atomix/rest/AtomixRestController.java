package fr.an.test.atomix.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.test.atomix.service.AtomixService;
import io.atomix.core.Atomix;
import io.atomix.core.value.AtomicValue;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path="/api/atomix")
@Slf4j
public class AtomixRestController {

	@Autowired
	private AtomixService delegate;
	
	@PostMapping("/atomicInt/{var}/getAndSet/{value}")
	public Integer getAndSetAtomicInt(@PathVariable("var") String var, @PathVariable("value") int value) {
		AtomicValue<Integer> atomicValue = getAtomicValueInteger(var);
		Integer prev = atomicValue.getAndSet(value);
		log.info("getAndSetAtomicInt " + var + " = " + value + " => prev:" + prev);
		return prev;
	}

	@GetMapping("/atomicInt/{var}")
	public Integer getAtomicInt(@PathVariable("var") String var) {
		AtomicValue<Integer> atomicValue = getAtomicValueInteger(var);
		Integer curr = atomicValue.get();
		log.info("getAtomicInt " + var + " => " + curr);
		return curr;
	}
	
	@PostMapping("/atomicInt/{var}/set/{value}")
	public void setAtomicInt(@PathVariable("var") String var, @PathVariable("value") int value) {
		log.info("setAtomicInt " + var + " = " + value);
		AtomicValue<Integer> atomicValue = getAtomicValueInteger(var);
		atomicValue.set(value);
	}
	
	private AtomicValue<Integer> getAtomicValueInteger(String var) {
		Atomix atomix = delegate.getAtomix();
		AtomicValue<Integer> atomicValue = atomix.<Integer>atomicValueBuilder(var)
				.withProtocol(MultiRaftProtocol.builder()
						.withReadConsistency(ReadConsistency.LINEARIZABLE)
						.build())
				.build();
		return atomicValue;
	}
	
}
