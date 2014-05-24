# Potentially unconventional makefile, but whatever.
#   Author: Ashley Newson <ashleynewson@smartsim.org.uk>

NULL=

JAVAC=javac

JAVA_SOURCE_FILES_PSIRSERVER= \
	psirserver/PSIRServer.java \
	$(NULL)

JAVA_SOURCE_FILES_REQUESTLISTENER= \
	requestlistener/RequestListener.java \
	requestlistener/ProtocolMismatchException.java \
	requestlistener/UnhandledClientException.java \
	requestlistener/RequestHandlerReg.java \
	$(NULL)

JAVA_SOURCE_FILES_REQUESTHANDLER= \
	requesthandler/RequestHandler.java \
	requesthandler/AdminRequestHandler.java \
	$(NULL)

JAVA_SOURCE_FILES_SESSION= \
	session/Session.java \
	$(NULL)

JAVA_SOURCE_FILES= \
	$(JAVA_SOURCE_FILES_PSIRSERVER) \
	$(JAVA_SOURCE_FILES_REQUESTLISTENER) \
	$(JAVA_SOURCE_FILES_REQUESTHANDLER) \
	$(JAVA_SOURCE_FILES_SESSION) \
	$(NULL)

all:
	$(JAVAC) $(JAVA_SOURCE_FILES)
