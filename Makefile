test:
	@for d in $(shell ls -d ${PWD}/*/ | grep day_); do \
		cd $${d} && echo '\nTesting:' && pwd && java MainTest.java time > output.txt && java Main.java >> output.txt && java MainTest.java && rm output.txt; \
	done

.PHONY: test
