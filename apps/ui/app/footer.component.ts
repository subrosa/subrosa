import {Component} from 'angular2/core';

@Component({
    selector: 'sr-footer',
    template: `
    <!--
<footer class="footer">
  <div layout="column">
    <span flex></span>
    <span class="md-caption">Your mom is &copy; 2016</span>
  </div>
</footer>
-->
    `,
    styles: [`
.footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  /* Set the fixed height of the footer here */
  height: 60px;
  line-height: 60px; /* Vertically center the text there */
  background-color: #f5f5f5;
}
    `]
})
export class FooterComponent { }
