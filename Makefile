# Compiler and flags
JAVAC = javac
JAVA = java
JFLAGS = -g
SRC_DIR = src
BIN_DIR = bin

# OS-specific commands
MKDIR = mkdir
RM = rm -rf

ifeq ($(OS),Windows_NT)
	MKDIR = mkdir
	RM = rmdir /s /q
endif

# Source files
SOURCES = $(shell find $(SRC_DIR) -name "*.java")
CLASSES = $(SOURCES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)

# Default target: compile and run
all: run

# Rule to compile .java files to .class files
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	@$(MKDIR) -p $(dir $@)
	$(JAVAC) $(JFLAGS) -d $(BIN_DIR) -cp $(SRC_DIR) $<

run: $(CLASSES)
	$(JAVA) -Xmx5G -cp $(BIN_DIR) Main $(ARGS)

clean:
	@$(RM) $(BIN_DIR)

.PHONY: all run clean