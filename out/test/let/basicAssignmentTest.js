const x = (5 + 7);
const y = (7 + x);
const result = ('The result is: ' + y);
const check = (x === 12);
const condition = ((15 + 4) === y) ? 'pass' : 'fail';
const sum = function (l,r){
  const ans = (l + r);
  return ans;
};
const num = sum(6, 4);
const recurse = function (num,times,ans){
  return (times === 0) ? ans : recurse(num, (times - 1), (num + ans));
};
const mul = recurse(3, 4, 0);
const complexTest = ((mul === 10) || (num === 10)) ? 'pass' : 'fail';
const pow = (2 ** 4);
const double = function (num){
  return (num * 2);
};
const op = function (num,fun){
  return (fun(num) * fun(num));
};
const higher = op(2, double);
const _ = print('This is a test: ', higher);
const cons = 1;
const neg = ( - 1);
const sub = (2 + neg);
const negMul = (3 * ( - 2));
const invert = !((1 === 1)) ? 'fail' : 'pass';
const another = sum(( - 1), ( - (2 - ( - 5))));
