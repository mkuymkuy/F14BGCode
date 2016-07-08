package com.f14.F14bgClient.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块版本对象
 * 
 * @author F14eagle
 *
 */
public class ModuleVersion {
	protected String moduleName;
	protected String moduleVersion;
	protected Map<String, FileVersion> fileVersions = new LinkedHashMap<String, FileVersion>();
	protected StringBuffer moduleFileContent = new StringBuffer(32);
	
	public ModuleVersion(String moduleName){
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		return moduleName;
	}
	
	public String getModuleVersion() {
		return moduleVersion;
	}

	public StringBuffer getModuleFileContent() {
		return moduleFileContent;
	}

	/**
	 * 从文件中装载版本信息
	 * 
	 * @param file
	 * @throws Exception 
	 */
	public void loadFile(File file) throws Exception{
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		while((str=br.readLine())!=null){
			this.moduleFileContent.append(str+"\n");
		}
		br.close();
		reader.close();
		this.loadFromContent();
	}
	
	/**
	 * 从字符串装载版本信息
	 * 
	 * @param str
	 */
	public void loadFromString(String str){
		this.moduleFileContent = new StringBuffer(str);
		this.loadFromContent();
	}
	
	/**
	 * 从版本内容装载版本信息
	 */
	protected void loadFromContent(){
		String[] line = this.moduleFileContent.toString().split("\n");
		for(int i=0;i<line.length;i++){
			if(i==0){
				//第一行为模块的版本号
				this.moduleVersion = line[i].trim();
			}else{
				//起始#的被注释了
				if(!line[i].startsWith("#")){
					//文件版本格式为 文件路径|子版本号
					String s[] = line[i].trim().split("\\|");
					if(s.length==2){
						FileVersion v = new FileVersion();
						v.path = s[0].trim();
						v.version = s[1].trim();
						this.addFileVersion(v);
					}
				}
			}
		}
	}
	
	/**
	 * 添加子文件版本
	 * 
	 * @param v
	 */
	protected void addFileVersion(FileVersion v){
		this.fileVersions.put(v.path, v);
	}
	
	/**
	 * 取得子文件的版本
	 * 
	 * @param path
	 * @return
	 */
	public String getFileVersion(String path){
		FileVersion fv = this.fileVersions.get(path);
		if(fv==null){
			return null;
		}else{
			return fv.version;
		}
	}
	
	/**
	 * 设置子文件的版本
	 * 
	 * @param path
	 * @param version
	 */
	public void setFileVersion(String path, String version){
		FileVersion v = this.fileVersions.get(path);
		if(v==null){
			v = new FileVersion();
			v.path = path;
			this.fileVersions.put(path, v);
		}
		v.version = version;
	}
	
	/**
	 * 取得当前版本中和入参版本不同子版本的文件名列表
	 * 
	 * @param v
	 * @return
	 */
	public List<String> getDifferentFiles(ModuleVersion v){
		List<String> res = new ArrayList<String>();
		for(FileVersion o : this.fileVersions.values()){
			if(!o.version.equals(v.getFileVersion(o.path))){
				res.add(o.path);
			}
		}
		return res;
	}
	
	/**
	 * 将版本信息转换成字符串
	 * 
	 * @return
	 */
	public String toVersionString(){
		String n = "\n";
		String s = "|";
		StringBuffer sb = new StringBuffer(32);
		sb.append(this.moduleVersion).append(n);
		for(FileVersion v : this.fileVersions.values()){
			sb.append(v.path).append(s).append(v.version).append(n);
		}
		return sb.toString();
	}
	
	/**
	 * 模块中的文件版本对象
	 * 
	 * @author F14eagle
	 *
	 */
	protected static class FileVersion{
		String path;
		String version;
	}
}
