import { keyframes, css } from "styled-components"
import { lighten, darken, rgba, transparentize } from "polished"
import { createGlobalStyle } from "styled-components"

const theme = {
  breakpoints: ["62em"],
  fontSizes: [11, 12, 13, 16],
  fonts: {
    sans: "Trebuchet MS, Tahoma, Arial, sans-serif",
    mono: "Menlo, monospace",
    heading: "system-ui, sans-serif"
  },
  space: [0, 4, 8, 16, 32, 64, 128],
  fontWeights: {
    normal: 500,
    bold: 700
  },
  colors: {
    black: "#000",
    white: "#fff",
    backgrounds: [
      "#131722",
      "#282b38",
      "#2F3241",
      "#343747",
      lighten(0.1, "#343747")
    ],
    canvas: "#131722",
    inputBg: "#131722",
    disabled: darken(0.3, "#aaa"),
    disabledBg: lighten(0.03, "#2F3241"),
    fore: "#aaa",
    emphasis: "#3BB3E4",
    deemphasis: "#555",
    heading: "#fff",
    toolbar: "white",
    boxBorder: "#131722",
    link: "#3BB3E4",

    alert: "#EB4D5C",
    success: "#53B987",

    sell: "#ef5451",
    buy: "#2aa599",

    inputBorder: rgba(0, 0, 0, 0.3),
    inputBorderError: "#EB4D5C"
  },
  radii: [0, 1, 4],
  keyFrames: {
    flashGreen: props => keyframes`
      from {
        background-color: ${props.theme.colors.buy};
        color: black;
      }
    
      to {
        background-color: none;
        color: ${props.theme.colors.fore};
      }
    `,
    flashRed: props => keyframes`
      from {
        background-color ${props.theme.colors.sell};
        color: black;
      }
    
      to {
        background-color: none;
        color: ${props.theme.colors.fore};
      }
    `
  },
  media: {
    handheld: (...args) => css`
      @media (max-width: 420px) {
        ${css(...args)};
      }
    `
  }
}

export const GlobalStyle = createGlobalStyle`
a {
  color: ${props => props.theme.colors.fore};
  &:hover {
    color: ${props => props.theme.colors.link};
  }
  > * {
    color: inherit !important;
  }
}
body {
  color: ${props => props.theme.colors.fore};
  font-family: ${props => props.theme.fonts.sans};
}
h1 {
  color: white;
  font-size: 22px;
  font-weight: bold;
}
i {
  color: inherit !important;
}

.oco-deemphasis {
  color: ${props => props.theme.colors.deemphasis} !important;
}

.oco-emphasis {
  color: ${props => props.theme.colors.emphasis} !important;
  text-shadow: 0px 0px 5px ${props => darken(0.2, props.theme.colors.emphasis)};
}

.oco-buy {
  color: ${props => props.theme.colors.buy};
}

.oco-sell {
  color: ${props => props.theme.colors.sell};
}

.oco-CANCELED {
  opacity: 0.4;
}

.oco-PENDING_NEW {
  box-shadow: 0px 0px 40px ${props =>
    transparentize(0.6, props.theme.colors.emphasis)} inset;
}

.ReactTable {
  position: relative;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.ReactTable * {
  box-sizing: border-box;
  font-size: 12px;
}
.ReactTable .rt-table {
  -webkit-box-flex: 1;
  -ms-flex: auto 1;
  flex: auto 1;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  -webkit-box-align: stretch;
  -ms-flex-align: stretch;
  align-items: stretch;
  border-collapse: collapse;
  width: 100%;
  overflow: visible;
}
.ReactTable .rt-thead {
  -webkit-box-flex: 1;
  -ms-flex: 1 0 auto;
  flex: 1 0 auto;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  background: rgb(255,255,255,0.06);
}
.ReactTable .rt-thead.-headerGroups {
  background: rgba(0, 0, 0, 0.03);
}
.ReactTable .rt-thead.-filters {
}
.ReactTable .rt-thead.-filters input,
.ReactTable .rt-thead.-filters select {
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: #fff;
  padding: 5px 7px;
  border-radius: 3px;
  font-weight: normal;
  outline: none;
}
.ReactTable .rt-thead.-filters .rt-th {
  border-right: 1px solid rgba(0, 0, 0, 0.02);
}
.ReactTable .rt-thead.-header {
  box-shadow: 0 2px 15px 0 rgba(0, 0, 0, 0.15);
}
.ReactTable .rt-thead .rt-tr {
  text-align: center;
}
.ReactTable .rt-thead .rt-th,
.ReactTable .rt-thead .rt-td {
  font-weight: bold;
  padding: 4px 8px;
  line-height: normal;
  position: relative;
  border-right: 1px solid rgba(0, 0, 0, 0.3);
  transition: box-shadow 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: inset 0 0 0 0 transparent;
}
.ReactTable .rt-thead .rt-th.-sort-asc,
.ReactTable .rt-thead .rt-td.-sort-asc {
  box-shadow: inset 0 3px 0 0 ${props => props.theme.colors.emphasis};
}
.ReactTable .rt-thead .rt-th.-sort-desc,
.ReactTable .rt-thead .rt-td.-sort-desc {
  box-shadow: inset 0 -3px 0 0 ${props => props.theme.colors.emphasis};
}
.ReactTable .rt-thead .rt-th.-cursor-pointer,
.ReactTable .rt-thead .rt-td.-cursor-pointer {
  cursor: pointer;
}
.ReactTable .rt-thead .rt-th:last-child,
.ReactTable .rt-thead .rt-td:last-child {
  border-right: 0;
}
.ReactTable .rt-thead .rt-resizable-header {
  overflow: visible;
}
.ReactTable .rt-thead .rt-resizable-header:last-child {
  overflow: hidden;
}
.ReactTable .rt-thead .rt-resizable-header-content {
  overflow: hidden;
  text-overflow: ellipsis;
}
.ReactTable .rt-thead .rt-header-pivot {
  border-right-color: #f7f7f7;
}
.ReactTable .rt-thead .rt-header-pivot:after,
.ReactTable .rt-thead .rt-header-pivot:before {
  left: 100%;
  top: 50%;
  border: solid transparent;
  content: " ";
  height: 0;
  width: 0;
  position: absolute;
  pointer-events: none;
}
.ReactTable .rt-thead .rt-header-pivot:after {
  border-color: rgba(255, 255, 255, 0);
  border-left-color: #fff;
  border-width: 8px;
  margin-top: -8px;
}
.ReactTable .rt-thead .rt-header-pivot:before {
  border-color: rgba(102, 102, 102, 0);
  border-left-color: #f7f7f7;
  border-width: 10px;
  margin-top: -10px;
}
.ReactTable .rt-tbody {
  -webkit-box-flex: 99999;
  -ms-flex: 99999 1 auto;
  flex: 99999 1 auto;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  overflow: auto;
}
.ReactTable .rt-tbody .rt-tr-group {
  border-bottom: solid 1px rgba(0, 0, 0, 0.05);
}
.ReactTable .rt-tbody .rt-tr-group:last-child {
  border-bottom: 0;
}
.ReactTable .rt-tbody .rt-td {
  border-right: 1px solid rgba(0, 0, 0, 0.02);
}
.ReactTable .rt-tbody .rt-td:last-child {
  border-right: 0;
}
.ReactTable .rt-tbody .rt-expandable {
  cursor: pointer;
}
.ReactTable .rt-tr-group {
  -webkit-box-flex: 1;
  -ms-flex: 1 0 auto;
  flex: 1 0 auto;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  -webkit-box-align: stretch;
  -ms-flex-align: stretch;
  align-items: stretch;
}
.ReactTable .rt-tr {
  -webkit-box-flex: 1;
  -ms-flex: 1 0 auto;
  flex: 1 0 auto;
  display: -webkit-inline-box;
  display: -ms-inline-flexbox;
  display: inline-flex;
  border-bottom: 1px solid rgba(0,0,0,0.2);
}
.ReactTable .rt-th,
.ReactTable .rt-td {
  -webkit-box-flex: 1;
  -ms-flex: 1 0 0px;
  flex: 1 0 0;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding: 4px 8px;
  overflow: hidden;
  transition: 0.3s ease;
  transition-property: width, min-width, padding, opacity;
}
.ReactTable .rt-th.-hidden,
.ReactTable .rt-td.-hidden {
  width: 0 !important;
  min-width: 0 !important;
  padding: 0 !important;
  border: 0 !important;
  opacity: 0 !important;
}
.ReactTable .rt-expander {
  display: inline-block;
  position: relative;
  margin: 0;
  color: transparent;
  margin: 0 10px;
}
.ReactTable .rt-expander:after {
  content: "";
  position: absolute;
  width: 0;
  height: 0;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%) rotate(-90deg);
  transform: translate(-50%, -50%) rotate(-90deg);
  border-left: 5.04px solid transparent;
  border-right: 5.04px solid transparent;
  border-top: 7px solid rgba(0, 0, 0, 0.8);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
}
.ReactTable .rt-expander.-open:after {
  -webkit-transform: translate(-50%, -50%) rotate(0);
  transform: translate(-50%, -50%) rotate(0);
}
.ReactTable .rt-resizer {
  display: inline-block;
  position: absolute;
  width: 36px;
  top: 0;
  bottom: 0;
  right: -18px;
  cursor: col-resize;
  z-index: 10;
}
.ReactTable .rt-tfoot {
  -webkit-box-flex: 1;
  -ms-flex: 1 0 auto;
  flex: 1 0 auto;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  flex-direction: column;
  box-shadow: 0 0 15px 0 rgba(0, 0, 0, 0.15);
}
.ReactTable .rt-tfoot .rt-td {
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}
.ReactTable .rt-tfoot .rt-td:last-child {
  border-right: 0;
}
.ReactTable.-striped .rt-tr.-odd {
  background: rgba(0, 0, 0, 0.18);
}
.ReactTable.-highlight .rt-tbody .rt-tr:not(.-padRow):hover {
  background: rgba(0, 0, 0, 0.05);
}
.ReactTable .-pagination {
  z-index: 1;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
  justify-content: space-between;
  -webkit-box-align: stretch;
  -ms-flex-align: stretch;
  align-items: stretch;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  padding: 3px;
  box-shadow: 0 0 15px 0 rgba(0, 0, 0, 0.1);
  border-top: 2px solid rgba(0, 0, 0, 0.1);
}
.ReactTable .-pagination input,
.ReactTable .-pagination select {
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: #fff;
  padding: 5px 7px;
  border-radius: 3px;
  font-weight: normal;
  outline: none;
}
.ReactTable .-pagination .-btn {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  display: block;
  width: 100%;
  height: 100%;
  border: 0;
  border-radius: 3px;
  padding: 6px;
  color: rgba(0, 0, 0, 0.6);
  background: rgba(0, 0, 0, 0.1);
  transition: all 0.1s ease;
  cursor: pointer;
  outline: none;
}
.ReactTable .-pagination .-btn[disabled] {
  opacity: 0.5;
  cursor: default;
}
.ReactTable .-pagination .-btn:not([disabled]):hover {
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
}
.ReactTable .-pagination .-previous,
.ReactTable .-pagination .-next {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  text-align: center;
}
.ReactTable .-pagination .-center {
  -webkit-box-flex: 1.5;
  -ms-flex: 1.5;
  flex: 1.5;
  text-align: center;
  margin-bottom: 0;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  -ms-flex-direction: row;
  flex-direction: row;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -ms-flex-pack: distribute;
  justify-content: space-around;
}
.ReactTable .-pagination .-pageInfo {
  display: inline-block;
  margin: 3px 10px;
  white-space: nowrap;
}
.ReactTable .-pagination .-pageJump {
  display: inline-block;
}
.ReactTable .-pagination .-pageJump input {
  width: 70px;
  text-align: center;
}
.ReactTable .-pagination .-pageSizeOptions {
  margin: 3px 10px;
}
.ReactTable .rt-noData {
  display: block;
  transition: all 0.3s ease;
  z-index: 1;
  pointer-events: none;
  padding: 4px 8px;
  color: #aaaaaa;
}
.ReactTable .-loading {
  display: block;
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  z-index: -1;
  opacity: 0;
  pointer-events: none;
}
.ReactTable .-loading > div {
  position: absolute;
  display: block;
  text-align: center;
  width: 100%;
  top: 50%;
  left: 0;
  font-size: 15px;
  color: rgba(0, 0, 0, 0.6);
  -webkit-transform: translateY(-52%);
  transform: translateY(-52%);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}
.ReactTable .-loading.-active {
  opacity: 1;
  z-index: 2;
  pointer-events: all;
}
.ReactTable .-loading.-active > div {
  -webkit-transform: translateY(50%);
  transform: translateY(50%);
}
.ReactTable .rt-resizing .rt-th,
.ReactTable .rt-resizing .rt-td {
  transition: none !important;
  cursor: col-resize;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
`

export default theme