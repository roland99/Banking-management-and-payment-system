function setTwoNumberDecimal(event) {
    this.value = parseFloat(this.value).toFixed(2);
}

function submitAmountForm() {
    document.getElementById("amountValue").value = document.getElementById("amountValue").value * 100;
}