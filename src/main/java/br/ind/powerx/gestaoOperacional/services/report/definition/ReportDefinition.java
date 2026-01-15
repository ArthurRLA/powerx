package br.ind.powerx.gestaoOperacional.services.report.definition;

import java.util.List;

import br.ind.powerx.gestaoOperacional.services.report.renderer.DetailsRenderer;
import lombok.Getter;

@Getter
public class ReportDefinition<T>{

	private final String title;
	private final String date;
	private final List<GroupingLevel<T, ?>> groupingLevels;
	private final List<String> detailmentHeaders;
	private final List<Float> columnWidths;
	private final DetailsRenderer<T> detailsRenderer;
	
	
	private ReportDefinition (String title, String date, List<GroupingLevel<T, ?>> groupingLevels,
			List<String> detailmentHeaders, List<Float> columnWidths, DetailsRenderer<T> detailsRenderer){
		this.title = title;
		this.date = date;
		this.groupingLevels = groupingLevels;
		this.detailmentHeaders = detailmentHeaders;
		this.detailsRenderer = detailsRenderer;
		this.columnWidths = columnWidths;
	}
	
	public static class Builder<T> {
		private String title;
		private String date;
		private List<GroupingLevel<T, ?>> groupingLevels;
		private List<String> detailmentHeaders;
		private List<Float> columnWidths;
		private DetailsRenderer<T> detailsRenderer;
		
		
		public Builder<T> title(String title){
			this.title = title;
			return this;
		}
		
		public Builder<T> date(String date){
			this.date = date;
			return this;
		}
		
		public Builder<T> groupingLevels(List<GroupingLevel<T, ?>> groupingLevels){
			this.groupingLevels = groupingLevels;
			return this;
		}
		
		public Builder<T> detailmentHeader(List<String> detailmentHeaders){
			this.detailmentHeaders = detailmentHeaders;
			return this;
		}
		
		public Builder<T> columnWidths(List<Float> columnWidths){
			this.columnWidths = columnWidths;
			return this;
		}
		
		public Builder<T> detailsRenderer(DetailsRenderer<T> detailsRenderer){
			this.detailsRenderer = detailsRenderer;
			return this;
		}
		
		public ReportDefinition<T> build(){
			return new ReportDefinition<>(title, date, groupingLevels, detailmentHeaders, columnWidths, detailsRenderer);
		}
	}
	
}
