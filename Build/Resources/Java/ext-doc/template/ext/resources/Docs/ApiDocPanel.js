Ext.ns('Docs');


Docs.ApiDocPanel = Ext.extend(Ext.Panel, {
    closable: true,
    autoScroll:true,
    directLinkTpl : "<a href=\"{0}\" target=\"_blank\">{0}</a>",
    initComponent : function(){
        var ps = this.cclass.split('.');
        this.title = ps[ps.length-1];
        this.tbar = this.buildTbar();

        Docs.ApiDocPanel.superclass.initComponent.call(this);
    },
    buildTbar : function() {
        return ['->',{
            text: 'Config Options',
            handler: this.scrollToMember.createDelegate(this, ['configs']),
            iconCls: 'icon-config'
        },'-',{
            text: 'Properties',
            handler: this.scrollToMember.createDelegate(this, ['props']),
            iconCls: 'icon-prop'
        }, '-',{
            text: 'Methods',
            handler: this.scrollToMember.createDelegate(this, ['methods']),
            iconCls: 'icon-method'
        }, '-',{
            text: 'Events',
            handler: this.scrollToMember.createDelegate(this, ['events']),
            iconCls: 'icon-event'
        }, '-',{
            text: 'Direct Link',
            handler: this.onDirectLink,
            scope: this,
            iconCls: 'icon-fav'
        }, '-',{
            tooltip:'Hide Inherited Members',
            iconCls: 'icon-hide-inherited',
            enableToggle: true,
            scope: this,
            toggleHandler : function(b, pressed){
                 this.body[pressed ? 'addClass' : 'removeClass']('hide-inherited');
            }
        }, '-', {
            tooltip:'Expand All Members',
            iconCls: 'icon-expand-members',
            enableToggle: true,
            scope: this,
            toggleHandler : function(b, pressed){
                this.body[pressed ? 'addClass' : 'removeClass']('full-details');
            }
        }];
    },
    onDirectLink : function(){
        var link = String.format(
            this.directLinkTpl,
            document.location.href+'?class='+this.cclass
        );
        Ext.Msg.alert('Direct Link to ' + this.cclass,link);
    },
    
    scrollToMember : function(member){
        var el = Ext.get(this.cclass + '-' + member);
        if(el){
            var top = (el.getOffsetsTo(this.body)[1]) + this.body.dom.scrollTop;
            this.body.scrollTo('top', top-25, {
                duration:0.75,
                scope : this,
                callback: function() {
                    var tr = el.up('.expandable');
                    if (tr) {
                        tr.addClass('expanded');
                    }
                    this.hlMember(member);
                }
            });
        }
    },

	scrollToSection : function(id){
		var el = Ext.getDom(id);
		if(el){
			var top = (Ext.fly(el).getOffsetsTo(this.body)[1]) + this.body.dom.scrollTop;
			this.body.scrollTo('top', top-25, {duration:0.5, callback: function(){
                Ext.fly(el).next('h2').pause(0.2).highlight('#8DB2E3', {attr:'color'});
            }});
        }
	},

    hlMember : function(member){
        var el = Ext.fly(this.cclass + '-' + member);
        if(el){
            if (tr = el.up('tr')) {
                tr.highlight('#cadaf9');
            }
        }
    }
});

Ext.reg('docpanel', Docs.ApiDocPanel);