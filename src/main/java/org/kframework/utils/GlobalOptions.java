// Copyright (c) 2014-2016 K Team. All Rights Reserved.
package org.kframework.utils;

import org.kframework.utils.errorsystem.KException.ExceptionType;

import java.util.EnumSet;
import java.util.Set;

public final class GlobalOptions {

    public GlobalOptions() {}

    public GlobalOptions(boolean debug, Warnings warnings, boolean verbose) {
        this.debug = debug;
        this.warnings = warnings;
        this.verbose = verbose;
    }

    public GlobalOptions(boolean debug, Warnings warnings, boolean verbose, boolean warnings2errors) {
        this.debug = debug;
        this.warnings = warnings;
        this.verbose = verbose;
        this.warnings2errors = warnings2errors;
    }

    public static enum Warnings {
        /**
         * All warnings and errors
         */
        ALL(EnumSet.allOf(ExceptionType.class)),

        /**
         * All warnings and errors except hidden warnings
         */
        NORMAL(EnumSet.complementOf(EnumSet.of(ExceptionType.HIDDENWARNING))),

        /**
         * No warnings, only errors
         */
        NONE(EnumSet.of(ExceptionType.ERROR));

        private Warnings(Set<ExceptionType> types) {
            typesIncluded = types;
        }
        private Set<ExceptionType> typesIncluded;

        public boolean includesExceptionType(ExceptionType e) {
            return typesIncluded.contains(e);
        }
    }

    //@Parameter(names={"--help", "-h"}, description="Print this help message", help = true)
    public boolean help = false;

    //@Parameter(names={"--help-experimental", "-X"}, description="Print help on non-standard options.", help=true)
    public boolean helpExperimental = false;

    //@Parameter(names="--version", description="Print version information")
    public boolean version = false;

    //@Parameter(names={"--verbose", "-v"}, description="Print verbose output messages")
    public boolean verbose = false;

    //@Parameter(names="--debug", description="Print debugging output messages")
    public boolean debug = false;

    //@Parameter(names={"--warnings", "-w"}, converter=WarningsConverter.class, description="Warning level. Values: [all|normal|none]")
    public Warnings warnings = Warnings.NORMAL;

    //@Parameter(names={"--warnings-to-errors", "-w2e"}, description="Convert warnings to errors.")
    public boolean warnings2errors = false;
}
