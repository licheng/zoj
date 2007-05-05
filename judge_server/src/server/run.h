#ifndef __RUN_H
#define __RUN_H

#include <set>
#include <string>

int doRun(int fdSocket,
          const std::string& programName,
          const std::string& sourceFileType,
          const std::string& stdinFilename,
          const std::string& stdoutFilename,
          int timeLiimt,
          int memoryLimit,
          int outputLimit);

int initAllowedFilesMap(const std::string& supportedSourceFileTypes);

#endif
