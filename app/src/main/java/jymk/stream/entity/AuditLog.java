package jymk.stream.entity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuditLog {

    public int error;

    @SerializedName("log_entries")
    public List<AuditLogEntry> log_entries;

    public AuditLog() {
    }

    @NonNull
    @Override
    public String toString() {
        return "AuditLog{" +
                "error=" + error +
                ", log_entries=" + log_entries +
                '}';
    }

    public static class AuditLogEntry {
        public long timestamp, requestor_ipv4;
        public int type;
        public String summary;

        public AuditLogEntry() {
        }

        @NonNull
        @Override
        public String toString() {
            return "AuditLogEntry{" +
                    "timestamp=" + timestamp +
                    ", requestor_ipv4=" + requestor_ipv4 +
                    ", type=" + type +
                    ", summary='" + summary + '\'' +
                    '}';
        }
    }
}
