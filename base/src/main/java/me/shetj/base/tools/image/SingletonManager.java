package me.shetj.base.tools.image;

import java.util.HashMap;
import java.util.Map;

public class SingletonManager{
    private static Map<String,Object> objMap = new HashMap<>();
    
    private SingletonManager(){}
    
    public static void registerService(String key,Object instance){
        if(!objMap.containsKey(key)){
            objMap.put(key,instance);
        }
    }
    
    public static Object getService(String key){
        return objMap.get(key);
    }
}
