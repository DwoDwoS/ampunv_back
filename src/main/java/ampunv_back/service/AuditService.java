package ampunv_back.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    public void logDataAccess(String userId, String action, String resource) {
        logger.info("AUDIT: User {} performed {} on {}", userId, action, resource);
    }

    public void logSensitiveDataAccess(String userId, String dataType) {
        logger.warn("SENSITIVE DATA ACCESS: User {} accessed {}", userId, dataType);
    }

    public void logSecurityEvent(String event, String details) {
        logger.error("SECURITY EVENT: {} - {}", event, details);
    }
}