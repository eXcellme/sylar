package nam.doog.sylar.entity;

import java.util.Date;

/**
 * 招聘信息实体
 * @author bflee
 *
 */
public class Recruit {
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	private String companyName,
							  companyNature,
							  companyScale,
							  companyIndustry;
	private String jobDescription,
							  jobRequire,
							  jobName;
	private Exp exp ;
	public enum Exp{
		LOWER_THAN_ONE,ONE_THREE,THREE_FIVE,FIVE_TEN,MORE_THAN_TEN
	}
	private Date publishDate ;
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyNature() {
		return companyNature;
	}
	public void setCompanyNature(String companyNature) {
		this.companyNature = companyNature;
	}
	public String getCompanyScale() {
		return companyScale;
	}
	public void setCompanyScale(String companyScale) {
		this.companyScale = companyScale;
	}
	public String getCompanyIndustry() {
		return companyIndustry;
	}
	public void setCompanyIndustry(String companyIndustry) {
		this.companyIndustry = companyIndustry;
	}
	public Exp getExp() {
		return exp;
	}
	public void setExp(Exp exp) {
		this.exp = exp;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getJobRequire() {
		return jobRequire;
	}
	public void setJobRequire(String jobRequire) {
		this.jobRequire = jobRequire;
	}
							  
	public String toString(){
		return this.companyName+","+this.jobName ;
	}
}
