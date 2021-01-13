package org.jsoftware.tjconsole;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.beanutils.ConvertUtils;

/**
 * Connection context. What user is connected to (server, bean)
 *
 * @author szalik
 */
public class TJContext extends Observable {
    private MBeanServerConnection serverConnection;
    private ObjectName objectName;
    private final Map<String, Object> environment;
    private Object serverURL;
    private int exitCode = 0;

    public TJContext() {
        environment = new LinkedHashMap<String, Object>();
    }

    public Map<String, Object> getEnvironment() {
        return Collections.unmodifiableMap(environment);
    }

    public void setEnvironmentVariable(String key, Object value, boolean check) {
        if (check) {
            if (!environment.containsKey(key)) {
                throw new IllegalArgumentException("Invalid key - " + key);
            }
            Object old = environment.get(key);
            if (!old.getClass().equals(value.getClass())) {
                value = ConvertUtils.convert(value, old.getClass());
                if (!old.getClass().equals(value.getClass())) {
                    throw new IllegalArgumentException("Invalid value type - " + value.getClass().getName() + " should be "
                            + old.getClass().getName());
                }
            }
        }
        Object prev = environment.get(key);
        if ((value != null && !value.equals(prev)) || (value == null && prev != null)) {
            setChanged();
        }
        notifyObservers(new UpdateEnvironmentEvent(key, prev, value));
        environment.put(key, value);
    }

    public MBeanServerConnection getServer() {
        return serverConnection;
    }

    public void setServer(MBeanServerConnection serverConnection, String url) {
        this.serverConnection = serverConnection;
        this.serverURL = url;
        this.objectName = null;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public boolean isConnected() {
        return serverConnection != null;
    }

    public List<MBeanAttributeInfo> getAttributes()
            throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        if (serverConnection == null || objectName == null) {
            return Collections.emptyList();
        }
        MBeanInfo beanInfo = serverConnection.getMBeanInfo(objectName);
        return Arrays.asList(beanInfo.getAttributes());
    }

    public boolean isBeanSelected() {
        return objectName != null;
    }

    public Object getServerURL() {
        return serverURL;
    }

    public void fail(int code) {
        if (exitCode == 0) {
            exitCode = code;
        }
    }

    public int getExitCode() {
        return exitCode;
    }
}
