Ext.ns('Docs');

Docs.ApiViewport = Ext.extend(Ext.Viewport, {

    constructor : function(cfg) {
        cfg = Ext.apply(cfg ||{}, {
            layout:'border',
            items : this.buildItems()
        });

        this.constructor.superclass.constructor.call(this,cfg);
    },
    buildItems : function() {


        return [
             //North
             {
                cls: 'docs-header',
                height: 36,
                region:'north',
                xtype:'box',
                el:'header',
                border:false,
                margins: '0 0 5 0'
            },
            //West
            {
               xtype : 'apitreepanel',
               itemId : 'apiTreePanel',
               region:'west',
               split:true,
               width: 280,
               minSize: 175,
               maxSize: 500,
               margins:'0 0 5 5',
               cmargins:'0 0 0 0',
               collapsible: true,
               collapseMode:'mini',
               listeners : {
                   scope : this,
                   click : this.onApiTreePanelClick,
                   afterrender : function(treePanel) {
                       treePanel.expandPath('/root/apidocs');
                   }
               }
           },
           //Central
           {
                xtype : 'apitabpanel',
                itemId : 'apiTabPanel',
                id:'doc-body',
                region:'center',
                margins:'0 5 5 0',
                listeners : {
                    scope : this,
                    tabchange : this.onApiTabPanelTabChange,
                    render    : {
                        delay : 250,
                        fn    : this.onAfterRenderLoadClass
                    }
                }
           }

        ];
    },
    getApiTabPanel : function() {
        return this.getComponent('apiTabPanel');
    },
    getApiTreePanel : function() {
        return this.getComponent('apiTreePanel');
    },
    onAfterRenderLoadClass : function() {
        var page = window.location.href.split('?')[1];
        if(page){
            var ps  = Ext.urlDecode(page),
                cls = ps['class'];
            this.getApiTabPanel().loadClass('output/' + cls + '.html', cls, ps.member);
        }

        this.doLayout();
    },
    onApiTreePanelClick : function(node, evtObj) {
        if(node.isLeaf()){
           evtObj.stopEvent();
           this.getApiTabPanel().loadClass(node.attributes.href, node.id);
        }
    },
    onApiTabPanelTabChange : function(tp, tab){
       this.getApiTreePanel().selectClass(tab.cclass);
   }
});