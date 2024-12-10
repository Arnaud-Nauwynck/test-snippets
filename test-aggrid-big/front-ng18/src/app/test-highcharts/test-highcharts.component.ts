import { Component } from '@angular/core';
import Highcharts from 'highcharts';
import {HighchartsChartModule} from 'highcharts-angular';

@Component({
  selector: 'app-test-highcharts',
  standalone: true,
  imports: [HighchartsChartModule],
  templateUrl: './test-highcharts.component.html'
})
export class TestHighchartsComponent {

  Highcharts: typeof Highcharts = Highcharts;
  chartOptions: Highcharts.Options = {
    series: [{
      data: [1, 2, 3],
      type: 'line'
    }]
  };

}
