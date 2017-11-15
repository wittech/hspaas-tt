var HsChart = {};

HsChart.line = function(url, data) {
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.series))
				return;
			$('#container').highHsCharts({
		        title: {
		            text: options.title,
		            x: -20
		        },
		        xAxis: {
		            categories: options.xlable
		        },
		        yAxis: {
		            title: {
		                text: options.ylable
		            },
		            plotLines: [{
		                value: 0,
		                width: 1,
		                color: '#808080'
		            }]
		        },
		        tooltip: {
		            valueSuffix: options.unit
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            borderWidth: 0
		        },
		        series: options.series
		    });
		}
	});
};


/**
 * 饼图
 */
HsChart.pie = function(url, data) {
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.data))
				return;	
			$('#container').highHsCharts({
		        HsChart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false
		        },
		        title: {
		            text: options.title
		        },
		        tooltip: {
			    	formatter: function() {
			    		return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
			    	},
			    	percentageDecimals: 1
			    	//pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
			    },
			    plotOptions: {
			        pie: {
			            allowPointSelect: true,
			            cursor: 'pointer',
			            dataLabels: {
			                enabled: true,
			                color: '#000000',
			                connectorColor: '#000000',
			                formatter: function() {
			                	var name = this.point.name;
			                	if(name.length > 10){
			                		name = name.substring(0,10) + "..";
			                	}
			                    return '<b>'+ name +'</b>: '+ this.percentage.toFixed(2) +' %';
			                }
			            }
			        }
			    },
		        series: [{
		            type: 'pie',
		            name : options.name,
		            data: options.data
		        }]
		    });
		}
	});
};

/**
 * 饼图
 */
HsChart.pie1 = function(url, data) {
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.data))
				return;	
			$('#container').highHsCharts({
		        HsChart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false
		        },
		        title: {
		            text: options.title
		        },
		        tooltip: {
			    	formatter: function() {
			    		return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
			    	},
			    	percentageDecimals: 1
			    	//pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
			    },
			    plotOptions: {
			    	 pie: {
		                    allowPointSelect: true,
		                    cursor: 'pointer',
		                    dataLabels: {
		                        enabled: false
		                    },
		                    showInLegend: true
		            }
			    },
		        series: [{
		            type: 'pie',
		            name : options.name,
		            data: options.data
		        }]
		    });
		}
	});
};

/**
 * 柱状图(纵向)
 */
HsChart.column = function(url, data){
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.data))
				return;
			$('#container').highHsCharts({
		        HsChart: {
		            type: 'column'
		        },
		        title: {
		            text: options.title
		        },
		        xAxis: {
		            type: 'category',
		            labels: {
		                rotation: -45,
		                align: 'right',
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif'
		                }
		            }
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: options.ylable
		            }
		        },
		        legend: {
		            enabled: false
		        },
		        tooltip: {
		            valueSuffix: options.unit
		        },
		        series: [{
		            name: options.name,
		            data: options.data,
		            dataLabels: {
		                enabled: true,
		                rotation: -90,
		                color: '#FFFFFF',
		                align: 'right',
		                x: 4,
		                y: 10,
		                style: {
		                    fontSize: '13px',
		                    fontFamily: 'Verdana, sans-serif',
		                    textShadow: '0 0 3px black'
		                }
		            }
		        }]
		    });
		}
	});
};

HsChart.columnMultiply = function(url, data){
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.series))
				return;
			$('#container').highHsCharts({
		        HsChart: {
		            type: 'column'
		        },
		        title: {
		            text: options.title
		        },
		        xAxis: {
		            categories: options.xlable
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: options.ylable
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		                '<td style="padding:0"><b>{point.y:.f} '+ options.unit+'</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        plotOptions: {
		            column: {
		                pointPadding: 0.2,
		                borderWidth: 0
		            }
		        },
		        series: options.series
		    });
		}
	});
};

HsChart.bar = function(url, data){
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.data))
				return;
			$('#container').highHsCharts({
		        HsChart: {
		            type: 'bar'
		        },
		        title: {
		            text: options.title
		        },
		        xAxis: {
		            categories: options.ylable,
		            title: {
		                text: null
		            }
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: options.xlable,
		                align: 'high'
		            },
		            labels: {
		                overflow: 'justify'
		            }
		        },
		        tooltip: {
		            valueSuffix: options.unit
		        },
		        plotOptions: {
		            bar: {
		                dataLabels: {
		                    enabled: true
		                }
		            }
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'top',
		            x: -40,
		            y: 100,
		            floating: true,
		            borderWidth: 1,
		            shadow: true
		        },
		        credits: {
		            enabled: false
		        },
		        series: [{
		            name: options.name,
		            data: options.data
		        }]
		    });
		}
	});
};

HsChart.combo = function(url, data){
	Hs.ajax({
		url : url,
		data : data,
		beforeSend : HsChart.loading,
		success : function(options){
			if(!HsChart.validate(options.series))
				return;
			$('#container').highHsCharts({
		        title: {
		            text: options.title
		        },
		        xAxis: {
		            categories: options.xlable
		        },
		        labels: {
		            items: [{
		                html: options.ptitle,
		                style: {
		                    left: '50px',
		                    top: '18px',
		                    color: (HighHsCharts.theme && HighHsCharts.theme.textColor) || 'black'
		                }
		            }]
		        },
		        series: [{
		            type: 'column',
		            name: 'Jane',
		            data: [3, 2, 1, 3, 4]
		        }, {
		            type: 'column',
		            name: 'John',
		            data: [2, 3, 5, 7, 6]
		        }, {
		            type: 'column',
		            name: 'Joe',
		            data: [4, 3, 3, 9, 0]
		        },  {
		            type: 'pie',
		            name: 'Total consumption',
		            data: [{
		                name: 'Jane',
		                y: 13,
		                color: HighHsCharts.getOptions().colors[0] // Jane's color
		            }, {
		                name: 'John',
		                y: 23,
		                color: HighHsCharts.getOptions().colors[1] // John's color
		            }, {
		                name: 'Joe',
		                y: 19,
		                color: HighHsCharts.getOptions().colors[2] // Joe's color
		            }],
		            center: [100, 80],
		            size: 100,
		            showInLegend: false,
		            dataLabels: {
		                enabled: false
		            }
		        }]
		    });
		}
	});
};

/**
 * 加载中
 */
HsChart.loading = function() {
	$('#container').empty();
	$('#container').html("<img class='load' src='" + Context.base + "/image/loading.gif' /><br/><br/>数据加载中，请稍后...");
};

/**
 * 加载出错
 */
HsChart.error = function(responseText) {
	$('#container').empty();
	$('#container').html("<div class='error_tip'>数据加载错误," + responseText + "</div>");
};

/**
 * 验证数据是否为空
 */
HsChart.validate = function(data){
	if(data == null || data == '' || data == undefined || data.length == 0){
		$('#container').empty();
		$('#container').html("<div class='error_tip'>没查询到相应的数据！</div>");
		return false;
	}
	return true;
};
