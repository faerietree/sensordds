/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.utils;

/**
 *
 * @author matt
 */
public class StringUtils {
    
    /**
     * 
     * @param str 
     * @param target
     * @param replacement
     * @return
     */
    public static String replaceFirst(String str, String target, String replacement){
        
        int start = str.indexOf(target);
        int targetLen = target.length();
        
        String prefix = str.substring(0, start);
        String suffix = str.substring(start+targetLen);
        
        return prefix+replacement+suffix;
    }
    
    public static void main(String[] args){
        
        String expression = "z > 100 temp < %n";
        String param = "100";
        
        String result = StringUtils.replaceFirst(expression, "%n", param);
        System.out.println(result);
    }
    
}
