package com.minispring.webmvc;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView类
 * 保存MVC处理器返回的模型和视图
 * 注意这个类不保存或公开HttpServletRequest和HttpServletResponse对象
 * 体现Spring MVC的数据传输对象设计模式
 */
public class ModelAndView {
    
    /** 视图实例或视图名称String */
    private Object view;
    
    /** 模型Map */
    private Map<String, Object> model;
    
    /** 指示此实例是否已被清空的标志 */
    private boolean cleared = false;
    
    /**
     * 默认构造函数：可以稍后设置视图名称或View对象和模型
     */
    public ModelAndView() {
    }
    
    /**
     * 给定视图名称的构造函数
     * @param viewName 要呈现的视图的名称
     */
    public ModelAndView(String viewName) {
        this.view = viewName;
    }
    
    /**
     * 给定View对象和模型的构造函数
     * @param view 要呈现的View对象
     */
    public ModelAndView(Object view) {
        this.view = view;
    }
    
    /**
     * 给定视图名称和模型的构造函数
     * @param viewName 要呈现的视图的名称
     * @param model 模型名称作为键，模型对象作为值的Map
     */
    public ModelAndView(String viewName, Map<String, Object> model) {
        this.view = viewName;
        this.model = model;
    }
    
    /**
     * 给定视图名称、模型名称和模型对象的构造函数
     * @param viewName 要呈现的视图的名称
     * @param modelName 模型中单个条目的名称
     * @param modelObject 单个模型对象
     */
    public ModelAndView(String viewName, String modelName, Object modelObject) {
        this.view = viewName;
        addObject(modelName, modelObject);
    }
    
    /**
     * 设置此ModelAndView的视图名称
     */
    public void setViewName(String viewName) {
        this.view = viewName;
    }
    
    /**
     * 返回要呈现的视图名称，如果我们使用View对象，则返回null
     */
    public String getViewName() {
        return (this.view instanceof String ? (String) this.view : null);
    }
    
    /**
     * 设置此ModelAndView的View对象
     */
    public void setView(Object view) {
        this.view = view;
    }
    
    /**
     * 返回View对象，如果我们使用视图名称，则返回null
     */
    public Object getView() {
        return this.view;
    }
    
    /**
     * 返回此ModelAndView是否有视图，无论是通过视图名称还是通过直接View实例
     */
    public boolean hasView() {
        return (this.view != null);
    }
    
    /**
     * 返回此ModelAndView是否使用视图引用
     * 即它引用视图名称而不是View实例
     */
    public boolean isReference() {
        return (this.view instanceof String);
    }
    
    /**
     * 返回模型Map。永远不会为null，但如果没有添加任何东西，可能为空（即没有键）
     */
    public Map<String, Object> getModel() {
        return getModelMap();
    }
    
    /**
     * 返回模型Map。永远不会返回null
     */
    protected Map<String, Object> getModelMap() {
        if (this.model == null) {
            this.model = new HashMap<>();
        }
        return this.model;
    }
    
    /**
     * 向模型添加属性
     * @param attributeName 模型属性的名称（永远不为null）
     * @param attributeValue 模型属性值（可以为null）
     */
    public ModelAndView addObject(String attributeName, Object attributeValue) {
        getModelMap().put(attributeName, attributeValue);
        return this;
    }
    
    /**
     * 将提供的Map中的所有属性添加到模型中
     * @param modelMap 属性的Map
     */
    public ModelAndView addAllObjects(Map<String, Object> modelMap) {
        getModelMap().putAll(modelMap);
        return this;
    }
    
    /**
     * 清空此ModelAndView对象的状态
     * 对象将为空，就像通过默认构造函数构造一样
     */
    public void clear() {
        this.view = null;
        this.model = null;
        this.cleared = true;
    }
    
    /**
     * 返回此ModelAndView是否为空，即它不保存任何视图且不包含模型
     */
    public boolean isEmpty() {
        return (this.view == null && (this.model == null || this.model.isEmpty()));
    }
    
    /**
     * 返回此ModelAndView是否已被清空
     */
    public boolean wasCleared() {
        return (this.cleared && isEmpty());
    }
    
    @Override
    public String toString() {
        return "ModelAndView [view=" + formatView() + "; model=" + this.model + "]";
    }
    
    private String formatView() {
        if (this.view == null) {
            return null;
        }
        return isReference() ? "\"" + this.view + "\"" : "[" + this.view.getClass().getSimpleName() + "]";
    }
}
