/*
 * Decompiled with CFR 0_118.
 */
package com.projectkorra.projectkorra.util;

public class AbilityLoadable
implements Cloneable {
    private final String name;

    public AbilityLoadable(String name) {
        this.name = name;
    }

    public AbilityLoadable clone() {
        try {
            return (AbilityLoadable)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoadResult init() {
        return new LoadResult();
    }

    public boolean isInternal() {
        return false;
    }

    public final String getName() {
        return this.name;
    }

    public static final class LoadResult {
        private final Result result;
        private final String reason;

        public LoadResult() {
            this(Result.SUCCESS, "");
        }

        public LoadResult(String failReason) {
            this(Result.FAILURE, failReason);
        }

        public LoadResult(Result result, String reason) {
            this.result = result;
            this.reason = reason;
        }

        public String getReason() {
            return this.reason;
        }

        public Result getResult() {
            return this.result;
        }

        public static enum Result {
            FAILURE,
            SUCCESS;
            

        }

    }

}

