document.addEventListener("DOMContentLoaded", () => {
    const passengerForm = document.querySelector("#passenger");
    const driverForm = document.querySelector("#driver");

    if (passengerForm) {
        passengerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const username = passengerForm.querySelector("#username").value;
            const email = passengerForm.querySelector("#email").value;
            const password = passengerForm.querySelector("#password").value;
            const phone = passengerForm.querySelector("#phone").value;

            const params = new URLSearchParams({ username, email, password, phone });

            const res = await fetch(`http://localhost:8080/user/register/passenger?${params}`, {
                method: "POST"
            });

            const message = await res.text();
            alert(message);
            passengerForm.reset();
        });
    }

    if (driverForm) {
        driverForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const username = driverForm.querySelector("#d-username").value;
            const email = driverForm.querySelector("#d-email").value;
            const password = driverForm.querySelector("#d-password").value;
            const phone = "0000000000"; // Not present in form, adjust if needed
            const license = driverForm.querySelector("#d-license").value;
            const vehicleType = driverForm.querySelector("#d-vehicle").value;

            const params = new URLSearchParams({ username, email, password, phone, license, vehicleType });

            const res = await fetch(`http://localhost:8080/user/register/driver?${params}`, {
                method: "POST"
            });

            const message = await res.text();
            alert(message);
            driverForm.reset();
        });
    }
    
});
