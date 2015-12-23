EXAMPLES ?= client_api_example rdkafka_example rdkafka_performance rdkafka_example_cpp client_api_example_cpp proposal_example_cpp 

all: $(EXAMPLES)

include ../mklove/Makefile.base

CFLAGS += -I../src
CXXFLAGS += -I../src-cpp

# librdkafka must be compiled with -gstrict-dwarf, but rdkafka_example must not,
# due to some clang bug on OSX 10.9
CPPFLAGS := $(subst strict-dwarf,,$(CPPFLAGS))

client_api_example: ../src/librdkafka.a client_api_example.c
	$(CC) $(CPPFLAGS) $(CFLAGS) client_api_example.c -o $@ $(LDFLAGS) \
		../src/librdkafka.a $(LIBS)
	@echo "# $@ is ready"
	@echo "#"
	@echo "# Run producer (write messages on stdin)"
	@echo "./rdkafka_example -P -t <topic>"
	@echo ""
	@echo "#"
	@echo "# More usage options:"
	@echo "./rdkafka_example --help"


rdkafka_example: ../src/librdkafka.a rdkafka_example.c
	$(CC) $(CPPFLAGS) $(CFLAGS) rdkafka_example.c -o $@ $(LDFLAGS) \
		../src/librdkafka.a $(LIBS)
	@echo "# $@ is ready"
	@echo "#"
	@echo "# Run producer (write messages on stdin)"
	@echo "./rdkafka_example -P -t <topic> -p <partition>"
	@echo ""
	@echo "# or consumer"
	@echo "./rdkafka_example -C -t <topic> -p <partition>"
	@echo ""
	@echo "#"
	@echo "# More usage options:"
	@echo "./rdkafka_example --help"

rdkafka_performance: ../src/librdkafka.a rdkafka_performance.c
	$(CC) $(CPPFLAGS) $(CFLAGS) rdkafka_performance.c -o $@ $(LDFLAGS) \
		../src/librdkafka.a $(LIBS)
	@echo "# $@ is ready"
	@echo "#"
	@echo "# Run producer"
	@echo "./$@ -P -t <topic> -p <partition> -s <msgsize>"
	@echo ""
	@echo "# or consumer"
	@echo "./$@ -C -t <topic> -p <partition>"
	@echo ""
	@echo "#"
	@echo "# More usage options:"
	@echo "./$@ --help"


rdkafka_example_cpp: ../src-cpp/librdkafka++.a ../src/librdkafka.a rdkafka_example.cpp
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) rdkafka_example.cpp -o $@ $(LDFLAGS) \
		../src-cpp/librdkafka++.a ../src/librdkafka.a $(LIBS) -lstdc++
		
client_api_example_cpp: ../src-cpp/librdkafka++.a ../src/librdkafka.a client_api_example.cpp 
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) client_api_example.cpp -o $@ $(LDFLAGS) \
		../src-cpp/librdkafka++.a ../src/librdkafka.a $(LIBS) -lstdc++
		
proposal_example_cpp: ../src-cpp/librdkafka++.a ../src/librdkafka.a client_api_cpp_proposal.h client_api_cpp_proposal.cpp proposal.cpp
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) client_api_cpp_proposal.h client_api_cpp_proposal.cpp proposal.cpp -o $@ $(LDFLAGS) \
		../src-cpp/librdkafka++.a ../src/librdkafka.a $(LIBS) -lstdc++					

clean:
	rm -f $(EXAMPLES)

