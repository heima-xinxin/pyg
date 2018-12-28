package pojoGroup;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 规格组合实体类
 * @author lx
 *
 */
public class SpecificationVo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//规格对象
	private Specification specification;
	//规格属性对象
	private List<SpecificationOption> specificationOptionList;

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public List<SpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}

	public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}
}
