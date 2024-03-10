//
// Created by qwq233 on 2/24/2023.
//

#include "v2sign.h"

#include <jni.h>
#include <regex>
#include <cstring>
#include <string>
#include <string_view>

#include <unistd.h>
#include <errno.h>
#include <dirent.h>

#include <linux_syscall_support.h>

#include <android/log.h>
#include "../log.h"

namespace {
extern "C" bool checkSignature(uint8_t result) {
    // firebase::crashlytics::SetCustomKey("signature", "github");
    return true;
}
}

#undef __STRING
#undef STRING
