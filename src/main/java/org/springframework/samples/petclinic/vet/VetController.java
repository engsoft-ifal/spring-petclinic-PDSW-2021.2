/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private final VetRepository vets;

	private static final String VIEWS_VET_CREATE_OR_UPDATE_FORM = "vets/createOrUpdateVetForm";

	private static final String VIEWS_ADD_SPECIALTY = "vets/createOrUpdateSpecialtyForm";

	private static final String VIEWS_ADD_AVAILABLE_DAY = "vets/createOrUpdateAvailableDayForm";

	public VetController(VetRepository clinicService) {
		this.vets = clinicService;
	}

	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		Page<Vet> paginated = findPaginated(page);
		vets.getVetList().addAll(paginated.toList());
		return addPaginationModel(page, paginated, model);

	}

	@GetMapping("/vets/new")
	public String initCreationForm(Map<String, Object> model) {
		Vet vet = new Vet();
		model.put("vet", vet);
		return VIEWS_VET_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/vets/new")
	public String processCreationForm(@Valid Vet vet, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_VET_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.vets.save(vet);
			return "redirect:/vets/" + vet.getId();
		}
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		return "vets/vetList";
	}

	private Page<Vet> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return vets.findAll(pageable);
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vets.findAll());
		return vets;
	}

	@ModelAttribute("specialties")
	public Collection<Specialty> populateVetSpecialties() {
		return this.vets.findVetSpecialties();
	}

	@ModelAttribute("days")
	public Collection<Day> populateVetDays() {
		return this.vets.findVetAvailableDays();
	}

	@GetMapping("/vets/{vetId}")
	public ModelAndView showVet(@PathVariable("vetId") int vetId) {
		ModelAndView mav = new ModelAndView("vets/vetDetails");
		Vet vet = this.vets.findById(vetId);
		mav.addObject(vet);
		return mav;
	}

	@GetMapping("/vets/{vetId}/specialty/new")
	private String getSpecialtyForm(Model model) {
		model.addAttribute("specialties");
		return VIEWS_ADD_SPECIALTY;
	}

	@PostMapping(path = "/vets/{vetId}/specialty/new")
	private String submitSpecialty(@ModelAttribute("specialtyForm") SpecialtyForm specialtyForm, Model model,
			@PathVariable("vetId") int vetId) {
		Vet vet = this.vets.findById(vetId);
		Specialty spec = this.vets.findSpecialtyByName(specialtyForm.getSpecialty());
		vet.addSpecialty(spec);
		this.vets.save(vet);
		return "redirect:/vets/" + vet.getId();
	}

	// new
	@GetMapping("/vets/{vetId}/available-day/new")
	private String getDayForm(Model model) {
		model.addAttribute("days");
		return VIEWS_ADD_AVAILABLE_DAY;
	}

	@PostMapping(path = "/vets/{vetId}/available-day/new")
	private String submitDay(@ModelAttribute("DayForm") DayForm dayForm, Model model,
			@PathVariable("vetId") int vetId) {
		Vet vet = this.vets.findById(vetId);
		Day day = this.vets.findDayByName(dayForm.getDay());
		vet.addDays(day);
		this.vets.save(vet);
		return "redirect:/vets/" + vet.getId();
	}
	// endnew

	@GetMapping("/vets/{vetId}/edit")
	public ModelAndView editVet(@PathVariable("vetId") int vetId) {
		Vet vet = this.vets.findById(vetId);
		ModelAndView mav = new ModelAndView(VIEWS_VET_CREATE_OR_UPDATE_FORM);
		mav.addObject(vet);
		return mav;
	}

	@PostMapping("/vets/{vetId}/edit")
	public String processEditionForm(@Valid Vet updatedVet, BindingResult result, @PathVariable("vetId") int vetId) {
		if (result.hasErrors()) {
			return VIEWS_VET_CREATE_OR_UPDATE_FORM;
		}
		else {
			Vet vet = this.vets.findById(vetId);
			if (!vet.getFirstName().equals(updatedVet.getFirstName())
					|| !vet.getLastName().equals(updatedVet.getLastName())) {
				vet.setFirstName(updatedVet.getFirstName());
				vet.setLastName(updatedVet.getLastName());
				this.vets.save(vet);
			}
			return "redirect:/vets/" + vet.getId();
		}
	}

	@PostMapping("/vets/{vetId}/specialty/{specId}/delete")
	public String processEditionForm(@PathVariable("vetId") int vetId, @PathVariable("specId") int specId) {
		Vet vet = this.vets.findById(vetId);
		Specialty spec = this.vets.findSpecialtyById(specId);
		vet.removeSpecialty(spec);
		this.vets.save(vet);
		return null; // "redirect:/vets/" + vet.getId();
	}

	@PostMapping("/vets/{vetId}/available-day/{dayId}/delete")
	public String processRemoveAvailableDayForm(@PathVariable("vetId") int vetId, @PathVariable("dayId") int dayId) {
		Vet vet = this.vets.findById(vetId);
		Day day = this.vets.findDayById(dayId);
		vet.removeDays(day);
		this.vets.save(vet);
		return null; // "redirect:/vets/" + vet.getId();
	}

}
