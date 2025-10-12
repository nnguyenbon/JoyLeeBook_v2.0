const showGenreElement = document.getElementById("showGenre");
const genresElement = document.getElementById("genres");
const containerGenreElement = document.getElementById("containerGenre");
const listGenre = document.querySelectorAll("#containerGenre li");

// showGenreElement.addEventListener("click", () => {
//   containerGenreElement.classList.toggle("hidden");
//   containerGenreElement.classList.toggle("opacity-0");
//   showGenreElement.classList.toggle("rotate-90");
// });

function checkedGenre(inputElement) {
  console.log(inputElement.checked);
  if (inputElement.checked) {
    const label = `
                    <li class="inline-block bg-gray-300 py-0.5 px-2 rounded-sm text-xs h-full">
                      <span>${inputElement.name}</span>
                      <span
                        class="inline-flex items-center justify-center size-3 hover:bg-gray-500 hover:text-white rounded-full text-xs leading-none cursor-pointer transition duration-300 ease-in-out"
                        onclick='removeGenre(this)'
                        >
                        &times;
                      </span>
                    </li>
    `;

    genresElement.insertAdjacentHTML("beforeend", label);
  } else {
    const tags = genresElement.querySelectorAll("li span:first-child");
    tags.forEach((span) => {
      if (span.textContent.trim() === inputElement.name) {
        span.parentElement.remove();
      }
    });
  }
}

function removeGenre(genreRemoveElement) {
  console.log(genreRemoveElement);
  const liRemoveElement = genreRemoveElement.parentElement;

  const spanText = liRemoveElement.firstElementChild;
  const inputCurrent = document.querySelectorAll("#containerGenre li input");
  inputCurrent.forEach((input) => {
    if (input.name.trim() === spanText.textContent.trim()) {
      input.checked = false;
      console.log(123123123);
    }
    console.log(input);
  });
  console.log(spanText);
  console.log(liRemoveElement);
  liRemoveElement.remove();
}

function toggleIndicator(index, event) {
    event.preventDefault();
    const indicators = document.querySelectorAll("#indicator a");
    const slides = document.querySelectorAll("[id^='slide-']");
    const slideContainer = document.querySelector("#slide-container");

    indicators.forEach((indicator) => {
        indicator.classList.remove("bg-sky-500");
    });

    indicators[index - 1].classList.add("bg-sky-500");

    const targetSlide = slides[index - 1];
    if (!targetSlide) return;

    targetSlide.scrollIntoView({
        behavior: "smooth",
        inline: "start",
        block: "nearest",
    });

}

